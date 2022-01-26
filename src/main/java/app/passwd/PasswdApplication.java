package app.passwd;

import app.passwd.model.SystemConfig;
import app.passwd.model.UserAudioItem;
import app.passwd.repository.*;
import app.passwd.service.UserAudioitemService;
import app.passwd.storage.StorageProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class PasswdApplication implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(PasswdApplication.class);
    @Autowired
    SystemConfigRepository repository;

    SystemConfig sysconfig = new SystemConfig();



    ObjectMapper mapper = new ObjectMapper();

    @Value("${config}")
    private String configfile;
    @Autowired
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {

        SpringApplication.run(PasswdApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        File dir = new File("/tmp/audio");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String declare = "本程式僅提供ddps使用";
//
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


            //僅提供測試站台及台中市學校使用
            if (sysconfig.getAuthorize_endpoint().equals("http://api.cloudschool.tw/school-oauth/authorize") || sysconfig.getAuthorize_endpoint().equals("https://api.tc.edu.tw/school-oauth/authorize")) {
                //true
            } else {
                logger.error(declare);
                System.exit(SpringApplication.exit(context));
            }

            //必須先存入所有sysconfig 設定, 後面再判斷是否讀取其它設定檔存入資料庫
            repository.save(sysconfig);


            //處理ldap client


        } else {
            logger.error("缺少config.json檔");
            System.exit(SpringApplication.exit(context));
        }

        logger.info("服務成功啟動");
    }


}


