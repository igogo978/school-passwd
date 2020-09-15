package app.passwd.controller.admin;

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
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserLoginService userloginservice;

//    @Autowired
//    Oauth2Client client;
//

//
//    @Autowired
//    SystemConfigRepository sysconfigrepository;
//
//    @Autowired
//    LdapRepository ldaprepository;


    @GetMapping("/passwd/admin")
    public String userhome() {
        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        }

        return "admin/home";
    }


    @GetMapping("/admin")
    public String userhomeProxypass() {
//        if (!userloginservice.isLoggedin()) {
//            return "redirect:/";
//        }
        return userhome();
    }


}
