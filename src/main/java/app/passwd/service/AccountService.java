package app.passwd.service;

import app.passwd.ldap.model.ADUser;
import app.passwd.model.SchoolUser;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import com.fasterxml.jackson.core.json.JsonReadFeature;
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
import java.util.Optional;

@Service
public class AccountService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    public SchoolUser getStaffUser(String username) throws IOException {
        List<SchoolUser> users = getAllStaffUsers();
        Optional<SchoolUser> user = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
        return user.get();
    }

    public List<SchoolUser> getAllStaffUsers() throws IOException {
        List<SchoolUser> users = new ArrayList<>();
        String endpoint = sysconfigrepository.findBySn(1).getSemesterdata_endpoint();
        String token = client.getAccesstoken();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        String data = semesterdata.getdata(token, endpoint);
        JsonNode root = mapper.readTree(data);
        JsonNode node = root.get("學期教職員");
        for (int i = 0; i < node.size(); i++) {
            SchoolUser user = new SchoolUser();
            String ou = node.get(i).get("處室").asText();
            String personalTitle = node.get(i).get("職稱").asText();
            String name = node.get(i).get("姓名").asText();
            String account = node.get(i).get("帳號").asText();
            user.setName(name);
            user.setUsername(account);
            user.setAdusername(account);
            user.setPersonalTitle(personalTitle);
            user.setPhysicalDeliveryOfficeName(ou);
            users.add(user);
        }


        return users;
    }




    public List<SchoolUser> getAllCSStudentUser() throws IOException {
        List<SchoolUser> users = new ArrayList<>();

        //取得cs 上 全部學生資料
        String endpoint = sysconfigrepository.findBySn(1).getSemesterdata_endpoint();
        String token = client.getAccesstoken();

        String data = semesterdata.getdata(token, endpoint);
//        logger.info("全部資料:" + data);
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
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
                    SchoolUser schoolUser = new SchoolUser();
                    //logger.info("班級：" + classno);
                    //logger.info("座號：" + classnode.get(j).get("座號").asText());
                    //logger.info("姓名：" + classnode.get(j).get("姓名").asText());
                    //logger.info("學號：" + classnode.get(j).get("學號").asText());
                    schoolUser.setName(classnode.get(j).get("姓名").asText());
                    schoolUser.setClassno(classno);
                    String username = null;
                    username = String.format("%s-%s", classnode.get(j).get("學號").asText().substring(0, 3), classnode.get(j).get("學號").asText());
                    schoolUser.setUsername(username);
                    if (ldaprepository.findBySn(1).getStuidRegular()) {
                        //logger.info("帳號為regular");
                        schoolUser.setAdusername(username);

                    } else {
                        //ad帳號為simple
                        schoolUser.setAdusername(classnode.get(j).get("學號").asText());
                    }
                    users.add(schoolUser);
                }
            }


        }
        return users;
    }

    //empty student accounts in win ad
    public List<SchoolUser> getEmptyStudentUser() throws IOException {
        //cloud school
        List<SchoolUser> csaccounts = getAllCSStudentUser();

        final List<ADUser> adusers = ldapTools.findAll();

//        adusers.forEach(user -> logger.info(user.getCn()));
        List<SchoolUser> emptyAccounts = new ArrayList<>();

        csaccounts.forEach(csaccount -> {

            if (!adusers.stream().anyMatch(aduser -> aduser.getCn().contains(csaccount.getAdusername()))) {
                emptyAccounts.add(csaccount);
            }
        });


        return emptyAccounts;

    }

    public List<SchoolUser> getEmptyUsers(String role) throws IOException {
        List<SchoolUser> csaccounts = new ArrayList<>();

        csaccounts = role.equals("staff")? getAllStaffUsers() : getAllCSStudentUser();
        final List<ADUser> adusers = ldapTools.findAll();
        List<SchoolUser> emptyAccounts = new ArrayList<>();

        csaccounts.forEach(csaccount -> {

            if (!adusers.stream().anyMatch(aduser -> aduser.getCn().contains(csaccount.getAdusername()))) {
                emptyAccounts.add(csaccount);
            }
        });

        return emptyAccounts;

    }




    public void createStudentAccounts(List<SchoolUser> accounts) {

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

    public void createAccounts(List<SchoolUser> accounts) throws IOException {
        String ou = "teacher";
        //單向建立WinAD上的帳號, 並且預設密碼demo1234
        //ldapTools.addStuUser();
        ldapTools.addUser(accounts,ou);


    }


}
