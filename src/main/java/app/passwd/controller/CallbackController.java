package app.passwd.controller;


import app.passwd.model.SystemConfig;
import app.passwd.model.User;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.Oauth2Client;
import app.passwd.service.UserLoginService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class CallbackController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Oauth2Client oauth2Client;

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    LdapRepository ldapRepository;

    @GetMapping("/passwd/callback")
    public RedirectView callback(@RequestParam(value = "state", required = true) String state, @RequestParam(value = "data", required = true) String data) throws IOException, URISyntaxException {
        assert oauth2Client.getState().equals(state);
        return getRedirectView(data);
    }


    @GetMapping("/callback")
    public RedirectView callbackproxypass(@RequestParam(value = "state", required = true) String state, @RequestParam(value = "data", required = true) String data) throws IOException, URISyntaxException {
        assert oauth2Client.getState().equals(state);
        return getRedirectView(data);
    }


    private RedirectView getRedirectView(String data) throws IOException, URISyntaxException {

        logger.info(String.format("3.取得code:%s", data));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(StringEscapeUtils.unescapeJava(data));
        String school_no = node.get("school_no").asText();
        String username = node.get("username").asText();
        String role = node.get("role").asText();
        String name = node.get("name").asText();
        String edu_key = node.get("edu_key").asText();
        String adusername = node.get("username").asText();

        //logger.info("account manager:"+ldapRepository.findBySn(1).getAccountManager());
        //學生要判斷在ad 上的帳號格式, regular or simple
        SystemConfig systemConfig = systemConfigRepository.findBySn(1);

        if (systemConfig.isSyncLdap() == Boolean.TRUE) {
            if (!ldapRepository.findBySn(1).getStuidRegular() && role.equals("student")) {
                adusername = node.get("username").asText().split("-")[1];
                logger.info("Student ad username:" + adusername);
            }

        }


        //String school_no, String username, String role, String name, String edu_key
        User user = new User(school_no, username, adusername, role, name, edu_key);

        //login session
        userloginservice.setUserLoggedin(Boolean.TRUE, user);

        //取得token
        //logger.info(sysconfig.getAccesstoken_endpoint());
        oauth2Client.setAccesstoken(systemConfig);

        if (username.equals(ldapRepository.findBySn(1).getAccountManager())) {
            return new RedirectView("/passwd/admin");
        }

        return new RedirectView("/passwd/userhome");
    }


}
