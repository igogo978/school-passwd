package app.passwd.controller;

import app.passwd.model.User;
import app.passwd.service.UserLoginService;
import app.passwd.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class Userhome2Controller {

    private final Logger logger = LoggerFactory.getLogger(Userhome2Controller.class);
    @Autowired
    UserLoginService userLoginService;
    @Autowired
    UserService userService;

    @GetMapping("/userhome2")
    public String userhome(Model model) {

        if (!userLoginService.isLoggedin()) {
            return "redirect:/";
        }

        String username = userLoginService.getUser().getUsername();
        User user = new User();
        if (userService.getUser(username).isPresent()) {
            user = userService.getUser(username).get();
        }

        model.addAttribute("user", user);
        return "userhome2";
    }


      @GetMapping("/useraudio2")
    public String useraudio(Model model, @RequestParam("code") Optional<Integer> code) {

        if (!userLoginService.isLoggedin()) {
            return "redirect:/";
        }


        String username = userLoginService.getUser().getUsername();
        User user = new User();
        String msg = "";
        Boolean showModal = Boolean.FALSE;
        if (userService.getUser(username).isPresent()) {
            user = userService.getUser(username).get();

            if (code.isPresent() && code.get() == 99) {
                msg = "file too large!";
                showModal = Boolean.TRUE;
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("msg", msg);
        model.addAttribute("showModal", showModal);
        return "useraudio2";
    }



}
