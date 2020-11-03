package app.passwd.controller.admin;

import app.passwd.repository.LdapRepository;
import app.passwd.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    LdapRepository ldapRepository;


    @GetMapping("/passwd/admin")
    public String userhome() {
        logger.info(userloginservice.getUser().getName());
        if (!userloginservice.isLoggedin() || !userloginservice.getUser().getUsername().equals(ldapRepository.findBySn(1).getAccountManager())) {
            logger.info("logging user: " + userloginservice.getUser());
            logger.info("account manager: " + ldapRepository.findBySn(1).getAccountManager());
            return "redirect:/";
        }

        return "admin/home";
    }


    @GetMapping("/admin")
    public String userhomeProxypass() {

        return userhome();
    }


}
