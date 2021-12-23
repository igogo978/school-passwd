package app.passwd.controller.admin;

import app.passwd.model.User;
import app.passwd.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    UserLoginService userloginservice;

    @GetMapping("/admin")
    public String userhome(Model model) {

        if (!userloginservice.isLoggedin()) {
            return "redirect:/";
        }

        User user = userloginservice.getUser();
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.equals("admin"));

        if(isAdmin) {

            model.addAttribute("user", user);
            return "admin/home";

        }
        return "redirect:/";
    }


}
