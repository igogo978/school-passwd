package app.passwd.api;

import app.passwd.ldap.model.ADUser;
import app.passwd.model.Account;
import app.passwd.model.SchoolUser;
import app.passwd.model.User;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.AccountService;
import app.passwd.service.Oauth2Client;
import app.passwd.service.LdapTools;
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

import javax.naming.InvalidNameException;
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
    LdapTools ldapTools;

    @Autowired
    AccountService accountService;

    @RequestMapping(value = "/passwd/username/{username}", method = RequestMethod.PUT)
    public String updatePasswd(@PathVariable("username") String username, @RequestBody Account account) throws IOException, InvalidNameException {
        return doUpdatePasswd(username, account);
    }


    @RequestMapping(value = "/username/{username}", method = RequestMethod.PUT)
    public String updatePasswdProxyPass(@PathVariable("username") String username, @RequestBody Account account) throws IOException, InvalidNameException {
        return doUpdatePasswd(username, account);
    }


    private String doUpdatePasswd(String username, Account account) throws IOException, InvalidNameException {
        String result = "";
        logger.info("update user passwd");

        if (!userloginservice.isLoggedin()) {
            return result;
        }
//        String role = userloginservice.getUser().getRole();

        String accesstoken = client.getAccesstoken();

        //send by array //    送到雲端校務帳號密碼,為固定格式,不可更改
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

        String url = sysconfigrepository.findBySn(1).getChangepasswd_endpoint();
        result = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class).getBody();
//            logger.info("new password:"+ account.getPassword());
        logger.info(result);

        //result {"_links":{"self":{"href":"https:\/\/api.tc.edu.tw\/change-password"}},"_embedded":{"change_password":[["107-10702 \u66f4\u6539\u4e86\u5bc6\u78bc123456"]]},"total_items":1}
        JsonNode node = mapper.readTree(result);
        if (node.get("total_items").asInt() == 1) {
            //成功更新, 登出
            userloginservice.setLoggedin(Boolean.FALSE);
        }

        //sync ldap
        if (sysconfigrepository.findBySn(1).isSyncLdap()) {
            User user = userloginservice.getUser();
            logger.info("ad username:" + user.getAdusername());

            if (ldapTools.isUserExist(user.getAdusername())) {
                logger.info("user already exists, update password");
                ADUser aduser = ldapTools.findByCn(user.getAdusername());
                ldapTools.updateUserPassword(aduser, account.getPassword());
            } else {

                logger.info("create user:" + user.getAdusername());
                if (user.getRole().equals("teacher")) {

        SchoolUser schoolUser = accountService.getStaffUser(user.getUsername());
                    ldapTools.addUser(user, account.getPassword(),"teacher", schoolUser);
                } else {
                    ldapTools.addStuUser(user, account.getPassword());

                }
            }

        }
        return result;
    }




}
