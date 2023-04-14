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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class UserhomeController {

    private final Logger logger = LoggerFactory.getLogger(UserhomeController.class);
    @Autowired
    UserLoginService userLoginService;
    @Autowired
    UserService userService;

//    @GetMapping("/passwd/userhome")
//    public String userhomeProxypass(Model model) {
//        return userhome(model);
//    }

    //    @GetMapping("/userhome")
//    public String userhome(Model model) {
//
//        if (!userLoginService.isLoggedin()) {
//            return "redirect:/";
//        }
//
//        String username = userLoginService.getUser().getUsername();
//        User user = new User();
//        if (userService.getUser(username).isPresent()) {
//            user = userService.getUser(username).get();
//        }
//
//        model.addAttribute("user", user);
//        return "userhome";
//    }
    @GetMapping("/useraudio")
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

        List<String> weekdays = new ArrayList<>();
        weekdays.add("1Mon");
        weekdays.add("2Tue");
        weekdays.add("3Wed");
        weekdays.add("4Thu");
        weekdays.add("5Fri");


        List<String> playtime = new ArrayList<>();
        playtime.add("0735");
        playtime.add("1210");
        playtime.add("1240");
        playtime.add("1250");
        playtime.add("1600");
        playtime.add("1610");

        model.addAttribute("user", user);
        model.addAttribute("msg", msg);
        model.addAttribute("showModal", showModal);

        model.addAttribute("weekdays", weekdays);
        model.addAttribute("playtime", playtime);
        return "useraudio";
    }

}
