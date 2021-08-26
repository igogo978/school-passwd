package app.passwd.api;

import app.passwd.model.UserItem;
import app.passwd.repository.UserItemRepository;
import app.passwd.service.UserLoginService;
import app.passwd.service.UseritemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class UserItemAPIController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    UserItemRepository userItemRepository;

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    UseritemService useritemService;

    @RequestMapping(value = "/api/useritem/{username}", method = RequestMethod.GET)
    public String getUserItem(@PathVariable("username") String username) throws JsonProcessingException {


        if (userLoginService.getUser() == null) {
            return "";
        }
        String loginUsername = userLoginService.getUser().getUsername();
        if (loginUsername.equals(username)) {
            Instant instant = Instant.now();
            List<UserItem> items = userItemRepository.findByExpiredOrExpiredGreaterThanAndUsername(0, instant.getEpochSecond(), username);
            return mapper.writeValueAsString(items);
        } else {
            return "";
        }

    }

    @RequestMapping(value = "/api/useritem/update", method = RequestMethod.PUT)
    public String updateUserItem(@RequestBody UserItem item){

        logger.info("update item expred time");
        Instant instant = Instant.now();
        Instant expired = instant.plus(item.getExpired(), ChronoUnit.DAYS);
        item.setExpired(expired.getEpochSecond());
        useritemService.update(item);
        return String.valueOf(item.getExpired());
    }
}
