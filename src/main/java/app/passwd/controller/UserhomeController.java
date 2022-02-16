package app.passwd.controller;

import app.passwd.model.SystemConfig;
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


    @GetMapping("/passwd/userhome")
    public String userhome(Model model) {

        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        } else {
//            logger.info(String.format("%s", userloginservice.isLoggedin()));
            String username = userloginservice.getUser().getUsername();
            String name = userloginservice.getUser().getName();
            String schoolid = userloginservice.getUser().getSchool_no();
            String role = userloginservice.getUser().getRole();
//            logger.info(client.getAccesstoken());
        }

        SystemConfig sysconfig = sysconfigrepository.findBySn(1);
        model.addAttribute("user", userloginservice.getUser());
//        https://bootsnipp.com/snippets/X2bG0
        return "userhome";
    }



    @GetMapping("/userhome")
    public String userhomeProxypass(Model model) {

        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        } else {
//            logger.info(String.format("%s", userloginservice.isLoggedin()));
            String username = userloginservice.getUser().getUsername();
            String name = userloginservice.getUser().getName();
            String schoolid = userloginservice.getUser().getSchool_no();
            String role = userloginservice.getUser().getRole();
//            logger.info(client.getAccesstoken());
        }

        SystemConfig sysconfig = sysconfigrepository.findBySn(1);
        model.addAttribute("user", userloginservice.getUser());
//        https://bootsnipp.com/snippets/X2bG0
        return "userhome";
    }


}
