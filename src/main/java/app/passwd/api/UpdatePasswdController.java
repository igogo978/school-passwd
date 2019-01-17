package app.passwd.api;

import app.passwd.model.Account;
import app.passwd.model.LdapClient;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.Oauth2Client;
import app.passwd.service.SmbLdap;
import app.passwd.service.UserLoginService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class UpdatePasswdController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Oauth2Client client;

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    SystemConfigRepository sysconfigrepository;

    @Autowired
    LdapRepository ldapRepository;

    @Autowired
    SmbLdap smbldap;


    @RequestMapping(value = "/passwd/{username}", method = RequestMethod.PUT)
    public String updatePasswd(@PathVariable("username") String username, @RequestBody Account account) throws IOException {
        String result = "";

        if (userloginservice.isLoggedin()) {
            String role = userloginservice.getUser().getRole();

            String accesstoken = client.getAccesstoken();

            //send by array
            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(account);
            ObjectMapper mapper = new ObjectMapper();
            //logger.info(mapper.writeValueAsString(accounts));
            //org.apache
            HttpClient httpClient = HttpClientBuilder.create().build();
            ClientHttpRequestFactory requestFactory
                    = new HttpComponentsClientHttpRequestFactory(httpClient);

            //spring.springframework
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            headers.set("Authorization", "Bearer " + accesstoken);
            HttpEntity<String> entity = new HttpEntity(accounts, headers);
            //String url = "https://api.tc.edu.tw/semester-data";
            String url = sysconfigrepository.findBySn(1).getChangepasswd_endpoint();
            result = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class).getBody();
//            logger.info("new password:"+ account.getPassword());
            logger.info(result);


            //result {"_links":{"self":{"href":"https:\/\/api.tc.edu.tw\/change-password"}},"_embedded":{"change_password":[["107-10702 \u66f4\u6539\u4e86\u5bc6\u78bc123456"]]},"total_items":1}
            JsonNode node = mapper.readTree(result);
            if (node.get("total_items").asInt() == 1) {
                userloginservice.setLoggedin(Boolean.FALSE);
            }

            //sync ldap
            if (sysconfigrepository.findBySn(1).isSyncLdap()) {
//                logger.info(String.format("%s:%s", account.getAccount(), smbldap.isUserExist(account.getAccount())));

                if (smbldap.isUserExist(account.getAccount())) {
                    //update user
                } else {
                    smbldap.addUser(account.getAccount(), account.getPassword(), role);

                }

                //extra ldap work
                //ldap-job.sh t0001 363626 teacher /home/teacher syncsmb
                String cwd = sysconfigrepository.findBySn(1).getCwd();
                String syncsmb = "syncsmb";

                if (ldapRepository.findBySn(1).getObjectclass().equals("sambaSamAccount")) {
                    syncsmb = "syncsmb";
                } else {
                    syncsmb = "nosyncsmb";
                }
//                logger.info(String.format("shell script,%s, %s", cwd, smbldap.findByUid(account.getAccount()).getUidNumber()));
                String[] cmd = {"sudo", cwd + "/ldap-job.sh", account.getAccount(), account.getPassword(), role, smbldap.findByUid(username).getHomeDirectory(), syncsmb};


                Process p = Runtime.getRuntime().exec(cmd);
            }


        }


        return result;
    }
}
