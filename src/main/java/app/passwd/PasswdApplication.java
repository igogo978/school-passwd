package app.passwd;

import app.passwd.ldap.model.ADUser;
import app.passwd.model.*;
import app.passwd.repository.LearningAccountRepository;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.LdapTools;
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

import javax.naming.ldap.LdapName;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PasswdApplication implements CommandLineRunner {

    //win ad
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${config}")
    private String configfile;

    @Autowired
    SystemConfigRepository repository;

//    @Autowired
//    LearningAccountRepository accountrepository;

    @Autowired
    LdapRepository ldaprepository;

    @Autowired
    LdapTools ldapTools;

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

        String declare = "本程式僅提供台中市學校使用";
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

            //僅提供測試站台及台中市學校使用

            if (sysconfig.getAuthorize_endpoint().equals("http://api.cloudschool.tw/school-oauth/authorize") || sysconfig.getAuthorize_endpoint().equals("https://api.tc.edu.tw/school-oauth/authorize")) {
                //true
            } else {
                logger.error(declare);
                System.exit(SpringApplication.exit(context));
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
                    ldapclient.setCert(node.get("cert").asText());
                    ldapclient.setUpnSuffix(node.get("upn_suffix").asText());
                    ldapclient.setAccountManager(node.get("accountManager").asText());
                    if (node.get("stu_id_format").asText().equals("simple")) {
                        ldapclient.setStuidRegular(Boolean.FALSE);
                    } else {
                        ldapclient.setStuidRegular(Boolean.TRUE);
                    }

                    JsonNode rolenode = node.get("roles");
                    rolenode.elements().forEachRemaining(e -> {
                        String role = e.get("role").asText();
                        String ou = e.get("ou").asText();
                        ldapclient.getRoles().add(new Role(role, ou));
                    });

                    ldaprepository.save(ldapclient);
                }
            } //is sync ldap


        } else {
            logger.error("缺少config.json檔");
            System.exit(SpringApplication.exit(context));
        }


        //測試連結ldap
        //信任憑證

        System.out.println("加入WinAD憑證:" + ldaprepository.findBySn(1).getCert());
        File cert = new File(String.format("%s/%s", System.getProperty("user.dir"), ldaprepository.findBySn(1).getCert()));

        if (cert.exists()) {
            System.setProperty("javax.net.ssl.trustStore", cert.getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
            System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
        } else {
            System.out.println("找不到憑證");
            System.exit(SpringApplication.exit(context));
        }

        //初始化, 檢查ou, 無則建立

        ldaprepository.findBySn(1).getRoles().forEach(role -> {
            System.out.println("檢查OU:" + role.getOu());
            if (!ldapTools.isOuExist(role.getOu())) {
                System.out.println("建立OU:" + role.getOu());
                ldapTools.createOu(role.getOu());
            }
        });


//        LdapContextSource source = new LdapContextSource();
//        String url = String.format("ldaps://%s:%s", ldaprepository.findBySn(1).getLdapserver(), ldaprepository.findBySn(1).getLdapport());
//        source.setUrl(url);
//        source.setBase(ldaprepository.findBySn(1).getBasedn());
//        source.setUserDn(ldaprepository.findBySn(1).getRootdn());
//        source.setPassword(ldaprepository.findBySn(1).getPasswd());
//        source.afterPropertiesSet();
//        LdapTemplate ldapTemplate = new LdapTemplate(source);
//        ldapTemplate.setIgnorePartialResultException(true);

        logger.info("帳號整合服務成功啟動");
        ldapTools.findAll();
    }


}


