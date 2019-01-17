package app.passwd.api;

import app.passwd.model.LearningAccount;
import app.passwd.repository.LearningAccountRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.Oauth2Client;
import app.passwd.service.SemesterData;
import app.passwd.service.UserLoginService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/passwd")
public class LearningAccountController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Oauth2Client client;

    @Autowired
    UserLoginService userloginservice;


    @Autowired
    LearningAccountRepository accountrepository;

    @Autowired
    SystemConfigRepository sysconfigrepository;

    @Autowired
    SemesterData semesterdata;

    @RequestMapping(value = "/account/{classnameseatno}", method = RequestMethod.GET)
    public LearningAccount findbyclassnameandseatno(@PathVariable String classnameseatno) throws IOException {
        LearningAccount account = new LearningAccount();
        String endpoint = new String();
        String token = new String();
        String classname;
        String seatno;

        if (userloginservice.isLoggedin()) {
            endpoint = sysconfigrepository.findBySn(1).getSemesterdata_endpoint();
            token = client.getAccesstoken();
        }
        String data = semesterdata.getdata(token, endpoint);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(data);
//        logger.info(node.get("學期編班").get(0).get("班名").asText());
        JsonNode node = root.get("學期編班");

        for (int i = 0; i < node.size(); i++) {
//            logger.info(node.get(i).get("年級").asText());
//            String grade = node.get(i).get("年級").asText();
            //101
            classname = String.format("%s%02d", node.get(i).get("年級").asText(), node.get(i).get("班序").asInt());
            JsonNode classnode = node.get(i).get("學期編班");
//            logger.info("學號：" + classnode.get(0).get("學號").asText());
            for (int j = 0; j < classnode.size(); j++) {

                logger.info("班級：" + classname);
                logger.info("座號：" + classnode.get(j).get("座號").asText());
                logger.info("姓名：" + classnode.get(j).get("姓名").asText());
//                logger.info("學號：" + classnode.get(j).get("學號").asText());
                seatno = classnode.get(j).get("座號").asText();
                if (classnameseatno.equals(classnode.get(j).get("學號").asText())) {
                    return accountrepository.findByClassnameAndSeatno(classname, seatno);
                }
            }


        }
        return account;
    }
}
