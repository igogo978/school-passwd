package app.passwd;

import app.passwd.ldap.PersonAttributesMapper;
import app.passwd.ldap.model.User;
import app.passwd.model.LdapClient;
import app.passwd.model.LearningAccount;
import app.passwd.model.Role;
import app.passwd.model.SystemConfig;
import app.passwd.repository.LearningAccountRepository;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.ReadLearningAccount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@SpringBootApplication
public class PasswdApplication implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${config}")
    private String configfile;

    @Autowired
    SystemConfigRepository repository;

    @Autowired
    LearningAccountRepository accountrepository;

    @Autowired
    LdapRepository ldaprepository;

    @Autowired
    private ConfigurableApplicationContext context;

//    @Autowired
//    ReadLearningAccount readaccount;

    SystemConfig sysconfig = new SystemConfig();
    List<LearningAccount> learningaccounts = new ArrayList<>();

    public static void main(String[] args) {

        SpringApplication.run(PasswdApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("學生更改密碼服務");
        logger.info("working dir:" + System.getProperty("user.dir"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        //確認設定檔
        if (new File(String.format("%s/%s", System.getProperty("user.dir"), configfile)).isFile()) {
            //create ObjectMapper instance

            node = mapper.readTree(new File(String.format("%s/%s", System.getProperty("user.dir"), configfile)));
            sysconfig.setSn(1);
            sysconfig.setClientid(node.get("clientid").asText());
            sysconfig.setSecret(node.get("secret").asText());
            sysconfig.setChangepasswd_endpoint(node.get("changepasswd_endpoint").asText());
            sysconfig.setAuthorize_endpoint(node.get("authorize_endpoint").asText());
            sysconfig.setAccesstoken_endpoint(node.get("accesstoken_endpoint").asText());
            sysconfig.setSemesterdata_endpoint(node.get("semesterdata_endpoint").asText());
            sysconfig.setCwd(System.getProperty("user.dir"));

            //是否更新ldap
            if (StringUtils.isEmpty(node.get("ldap").asText())) {
                sysconfig.setSyncLdap(Boolean.FALSE);
            } else {
                sysconfig.setSyncLdap(Boolean.TRUE);
            }


            //必須先存入所有sysconfig 設定, 後面再判斷是否讀取其它設定檔存入資料庫
            repository.save(sysconfig);

            //讀取學習帳號
//            if (StringUtils.isEmpty(node.get("account_info").asText())) {
//                sysconfig.setLearningAccount(Boolean.FALSE);
//            } else {
//                sysconfig.setLearningAccount(Boolean.TRUE);
//
//            }
//            logger.info(mapper.writeValueAsString(sysconfig));

            //處理學習帳號
//            if (sysconfig.isLearningAccount()) {
//                String accountfile = node.get("account_info").asText();
//                File file = new File((String.format("%s/%s", System.getProperty("user.dir"), accountfile)));
//                logger.info(String.format("%s/%s", System.getProperty("user.dir"), accountfile));
//                if (file.exists()) {
//                    List<LearningAccount> learningaccounts = new ArrayList<>();
//                    learningaccounts = readaccount.readxls(file);
//
//                    learningaccounts.forEach(account -> {
//                        accountrepository.save(account);
//                    });
//                }
//            }

            //處理ldap client

            if (sysconfig.isSyncLdap()) {
                String ldapconfigfile = node.get("ldap").asText();
                File file = new File((String.format("%s/%s", System.getProperty("user.dir"), ldapconfigfile)));
                if (file.exists()) {
                    node = mapper.readTree(new File(String.format("%s/%s", System.getProperty("user.dir"), ldapconfigfile)));
                    LdapClient ldapclient = new LdapClient();
                    ldapclient.setSn(1);
                    ldapclient.setBasedn(node.get("basedn").asText());
                    ldapclient.setLdapserver(node.get("ldap_server").asText());
                    ldapclient.setLdapport(node.get("ldap_port").asText());
                    ldapclient.setPasswd(node.get("passwd").asText());
                    ldapclient.setRootdn(node.get("rootdn").asText());


                    ldapclient.setObjectclass(node.get("sambaobjectclass").asText());
                    if (node.get("sambaobjectclass").asText().equals("sambaSamAccount")) {
                        logger.info("載入 ldap objectclass - sambaSamAccount ");
                    }

                    ldapclient.setSid(node.get("sid").asText());



                    JsonNode rolenode = node.get("role");
                    rolenode.elements().forEachRemaining(e -> {
                        String ou = e.get("ou").asText();
                        Integer gid = e.get("gid").asInt();
                        String home = e.get("home").asText();
                        ldapclient.getRoles().add(new Role(ou, gid, home));
                    });

                    ldaprepository.save(ldapclient);

                }


            } //is sync ldap


//           logger.info( accountrepository.findByClassnameAndSeatno("101","1").getName());

        } else {
            logger.error("缺少config.json檔");
            System.exit(SpringApplication.exit(context));
        }

        //valid ldap config and find max uidnumber
        LdapClient ldapclient = ldaprepository.findBySn(1);

        String url = String.format("ldap://%s:%s", ldapclient.getLdapserver(), ldapclient.getLdapport());
        String basedn = ldapclient.getBasedn();
        String rootdn = ldapclient.getRootdn();
        String password = ldapclient.getPasswd();

        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(basedn);
        source.setUserDn(rootdn);
        source.setPassword(password);
        source.afterPropertiesSet();


        LdapTemplate ldaptemplate = new LdapTemplate(source);
        List<User> users = ldaptemplate.search(
                query().where("objectclass").is("person"),
                new PersonAttributesMapper());

        Integer max = 5000;
        for (User user : users) {
            if (Integer.valueOf(user.getUidNumber()) < 50000 && Integer.valueOf(user.getUidNumber()) > max) {
                max = Integer.valueOf(user.getUidNumber());
            }

        }
        ldapclient.setUidNumber(max);

        ldaprepository.save(ldapclient);

    }


}


