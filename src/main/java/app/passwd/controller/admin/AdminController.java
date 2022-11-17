package app.passwd.controller.admin;

import app.passwd.repository.LdapRepository;
import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.UserLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    LdapRepository ldapRepository;


    @GetMapping("/passwd/admin")
    public String userhome(Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(userloginservice.getUser()));
        if (!userloginservice.isLoggedin() || !userloginservice.getUser().getUsername().equals(systemConfigRepository.findBySn(1).getAccountManager())) {
            return "redirect:/";
        }

        model.addAttribute("isSyncLdap",systemConfigRepository.findBySn(1).isSyncLdap());

        return "admin/home";
    }


    @GetMapping("/admin")
    public String userhomeProxypass(Model model) throws JsonProcessingException {

        return userhome(model);
    }


}
