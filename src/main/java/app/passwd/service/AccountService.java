package app.passwd.service;

import app.passwd.ldap.model.ADUser;
import app.passwd.model.Role;
import app.passwd.model.StudentUser;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LdapRepository ldaprepository;

    @Autowired
    SemesterData semesterdata;

    @Autowired
    SystemConfigRepository sysconfigrepository;

    @Autowired
    LdapTools ldapTools;

    @Autowired
    Oauth2Client client;


    public List<StudentUser> getAllCSStudentUser() throws IOException {
        List<StudentUser> users = new ArrayList<>();

        //取得cs 上 全部學生資料
        String endpoint = sysconfigrepository.findBySn(1).getSemesterdata_endpoint();
        String token = client.getAccesstoken();

        String data = semesterdata.getdata(token, endpoint);
//        logger.info("全部資料:" + data);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(data);
        JsonNode node = root.get("學期編班");
        logger.info("班級數:" + node.size());
        for (int i = 0; i < node.size(); i++) {
//            logger.info(node.get(i).get("年級").asText());
//            String grade = node.get(i).get("年級").asText();
            //101
            String classno = String.format("%s%02d", node.get(i).get("年級").asText(), node.get(i).get("班序").asInt());
//            logger.info(classno);
//            logger.info(String.valueOf(node.get(i).has("學期編班")));
            if (node.get(i).has("學期編班")) {
                JsonNode classnode = node.get(i).get("學期編班");

                //logger.info("學號：" + classnode.get(i).get("學號").asText());
                for (int j = 0; j < classnode.size(); j++) {
                    StudentUser studentUser = new StudentUser();
                    //logger.info("班級：" + classno);
                    //logger.info("座號：" + classnode.get(j).get("座號").asText());
                    //logger.info("姓名：" + classnode.get(j).get("姓名").asText());
                    //logger.info("學號：" + classnode.get(j).get("學號").asText());
                    studentUser.setName(classnode.get(j).get("姓名").asText());
                    studentUser.setClassno(classno);
                    String username = null;
                    username = String.format("%s-%s", classnode.get(j).get("學號").asText().substring(0, 3), classnode.get(j).get("學號").asText());
                    studentUser.setUsername(username);
                    if (ldaprepository.findBySn(1).getStuidRegular()) {
                        //logger.info("帳號為regular");
                        studentUser.setAdusername(username);

                    } else {
                        //ad帳號為simple
                        studentUser.setAdusername(classnode.get(j).get("學號").asText());
                    }
                    users.add(studentUser);
                }
            }


        }
        return users;
    }

    public List<StudentUser> getEmptyStudentUser() throws IOException {
        //cloud school
        List<StudentUser> csaccounts = getAllCSStudentUser();


        final List<ADUser> adusers = ldapTools.findAll();
//        Role role = ldaprepository.findBySn(1).getRoles().stream().filter(r -> r.getRole().contains("teacher")).findFirst().orElse(null);

        List<StudentUser> emptyAccounts = new ArrayList<>();

        adusers.forEach(user->{
            if (user.getCn().equals("102-102047")) {
                logger.info(user.getCn());
            }
        });

        csaccounts.forEach(csaccount -> {

            if (!adusers.stream().anyMatch(aduser -> aduser.getCn().contains(csaccount.getAdusername()))) {
                emptyAccounts.add(csaccount);
            }
        });
//        csaccounts.forEach(studentAccount -> {
//            if (!ldapTools.isUserExist(studentAccount.getAdusername())) {
//
//                emptyAccounts.add(studentAccount);
//            }
//        });


        return emptyAccounts;

    }

    public void createStudentAccounts(List<StudentUser> accounts) {

        //單向建立WinAD上的帳號, 並且預設密碼與帳號同
//        ldapTools.addStuUser();
        accounts.forEach(user -> {
            logger.info(String.format("建立學生帳號:%s", user.getAdusername()));
            try {
                    ldapTools.addStuUser(user, user.getAdusername());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

}
