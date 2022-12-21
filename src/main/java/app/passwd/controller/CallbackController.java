package app.passwd.controller;

import app.passwd.model.SystemConfig;
import app.passwd.model.User;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.repository.UserRepository;
import app.passwd.service.Oauth2Client;
import app.passwd.service.SemesterData;
import app.passwd.service.UserLoginService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CallbackController {

    private final Logger logger = LoggerFactory.getLogger(CallbackController.class);
    int quota = 0;

    @Autowired
    Oauth2Client client;

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    SystemConfigRepository repository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    SemesterData semesterData;


//    @GetMapping("/passwd/callback")
//    public RedirectView callback(@RequestParam(value = "state", required = true) String state, @RequestParam(value = "data", required = true) String data) throws IOException, OAuthProblemException, OAuthSystemException {
//        assert client.getState().equals(state);
//        return getRedirectView(data);
//    }


    @GetMapping("/callback")
    public RedirectView callback(@RequestParam(value = "state", required = true) String state, @RequestParam(value = "data", required = true) String data) throws IOException, OAuthProblemException, OAuthSystemException {
        assert client.getState().equals(state);
        return getRedirectView(data);
    }


    private RedirectView getRedirectView(String data) throws IOException, OAuthProblemException, OAuthSystemException {
        logger.info(String.format("3.取得code:%s", data));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(StringEscapeUtils.unescapeJava(data));
        String school_no = node.get("school_no").asText();
        String username = node.get("username").asText();
        String role = node.get("role").asText();
        List<String> roles = new ArrayList<>();
        roles.add(role);

        String name = node.get("name").asText();

        String edu_key = node.get("edu_key").asText();
//        String adusername = node.get("username").asText();

        logger.info("role: " + role);

        if(role.equals("student")) {

            return new RedirectView("403");
        }

        //String school_no, String username, String role, String name, String edu_key
        User user = new User();
        //is a new user? then, add
        if (userRepository.findByUsername(username).isEmpty()) {

            user = new User(school_no, username, roles, name, edu_key);
            user.setQuota(quota);
            user.setStatus(true);
            userRepository.save(user);
        } else {
//            username = "tea41";
            user = userRepository.findByUsername(username).get();
        }

        //login session
        userloginservice.setUserLogin(Boolean.TRUE, user);

        //取得token
        SystemConfig sysconfig = repository.findBySn(1);
        //logger.info(sysconfig.getAccesstoken_endpoint());
        client.setAccesstoken(sysconfig);


//        String alldata = semesterData.getdata(client.getAccesstoken(),sysconfig.getSemesterdata_endpoint());
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("alldata.txt"));
//        bufferedWriter.append(alldata);
//        bufferedWriter.close();

//        return new RedirectView("/passwd/userhome");
        return new RedirectView("userhome2");
    }


}
