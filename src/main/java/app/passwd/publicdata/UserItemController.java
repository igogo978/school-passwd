package app.passwd.publicdata;

import app.passwd.model.SyncUseritem;
import app.passwd.model.User;
import app.passwd.service.SyncUseritemService;
import app.passwd.service.UserLoginService;
import app.passwd.service.UserService;
import app.passwd.service.UseritemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserItemController {

    private final Logger logger = LoggerFactory.getLogger(UserItemController.class);

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    UserService userService;


    @Autowired
    UseritemService useritemService;

    @Autowired
    SyncUseritemService syncUseritemService;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/public/useritem")
    public String getUserItems(Model model) throws JsonProcessingException {
        User user = new User();
        Map<String, Long> useritemcount = new HashMap<>();
        SyncUseritem syncUseritem = new SyncUseritem();
        if (!userLoginService.isLoggedin()) {
            user.setUsername("guest");
            user.getRoles().add("guest");

        } else {

            String username = userLoginService.getUser().getUsername();
            user = userService.getUser(username).get();
        }

        syncUseritem = syncUseritemService.get("guard", "led");
        ZonedDateTime present = Instant.ofEpochSecond(syncUseritem.getTimestamp()).atZone(ZoneId.of("Asia/Taipei"));

        useritemcount = useritemService.count(Instant.now().getEpochSecond());
//        logger.info(mapper.writeValueAsString(useritemcount));
        model.addAttribute("user", user);
        model.addAttribute("syncTimestamp", "更新時間:" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(present));
        model.addAttribute("useritemcount", useritemcount);


        return "public/useritem";
    }
}
