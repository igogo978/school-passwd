package app.passwd.controller;

import app.passwd.model.SystemConfig;
import app.passwd.model.User;
import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.Oauth2Client;
import app.passwd.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserhomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Oauth2Client client;

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    SystemConfigRepository sysconfigrepository;

    @Autowired
    LdapRepository ldaprepository;


    @GetMapping("/passwd/userhome")
    public String userhome(Model model) {

        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        }

        model.addAttribute("user", userloginservice.getUser());
        return "userhome";
    }


    @GetMapping("/userhome")
    public String userhomeProxypass(Model model) {

        return userhome(model);
    }


}
