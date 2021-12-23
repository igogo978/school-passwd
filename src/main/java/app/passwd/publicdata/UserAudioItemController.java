package app.passwd.publicdata;

import app.passwd.model.User;
import app.passwd.model.UserAudioItem;
import app.passwd.model.UserItem;
import app.passwd.service.UserAudioitemService;
import app.passwd.service.UserLoginService;
import app.passwd.service.UserService;
import app.passwd.service.UseritemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserAudioItemController {

    private final Logger logger = LoggerFactory.getLogger(UserAudioItemController.class);

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    UserService userService;

    @Autowired
    UserAudioitemService userAudioitemService;

    @GetMapping("/public/useraudio")
    public String getUserItems(Model model) throws JsonProcessingException {
        User user = new User();
        Map<String,Long> useritemcount = new HashMap<>();
        if (!userLoginService.isLoggedin()) {
            user.setUsername("guest");
            user.getRoles().add("guest");

        } else {

            String username = userLoginService.getUser().getUsername();
            user = userService.getUser(username).get();

        }

        List<UserItem> items = userAudioitemService.getUseritemsEnabled(Instant.now().getEpochSecond());
//        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(useritemcount));
        model.addAttribute("user", user);
        model.addAttribute("items", items);


        return "public/useraudio";
    }
}
