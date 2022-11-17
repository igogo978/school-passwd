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
public class SyncController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Oauth2Client client;

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    LdapRepository ldaprepository;


    @GetMapping("/passwd/account/student")
    public String syncStudents(Model model) {
        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        }

        model.addAttribute("isSyncLdap",systemConfigRepository.findBySn(1).isSyncLdap());
        return "admin/syncstudent";
    }


    @GetMapping("/account/student")
    public String syncStudentsProxypass(Model model) {
        return syncStudents(model);
    }


    @GetMapping("/passwd/account/staff")
    public String syncStaff(Model model) {
        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        }
        return "admin/syncstaff";
    }


    @GetMapping("/account/staff")
    public String syncStaffProxypass(Model model) {
        return syncStaff(model);
    }


}
