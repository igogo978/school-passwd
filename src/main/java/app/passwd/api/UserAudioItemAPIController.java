package app.passwd.api;

import app.passwd.model.UserAudioItem;
import app.passwd.model.UserItem;
import app.passwd.repository.UserAudioitemRepository;
import app.passwd.service.UserAudioitemService;
import app.passwd.service.UserLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserAudioItemAPIController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    @Lazy
    UserAudioitemRepository userAudioitemRepository;

    @Autowired
    @Lazy
    UserAudioitemService userAudioitemService;

    @Autowired
    UserLoginService userLoginService;


    @RequestMapping(value = "/api/useraudioitem", method = RequestMethod.GET)
    public List<UserItem> getUserAudioItemEnabled() throws JsonProcessingException {
        return userAudioitemService.getUseritemsEnabled(Instant.now().getEpochSecond());

    }

    @RequestMapping(value = "/api/useraudioitem/id/{id}", method = RequestMethod.GET)
    public UserAudioItem getUserAudioItemByID(@PathVariable("id") String id) throws JsonProcessingException {
        return userAudioitemService.getUserAudioItemById(id);
    }


    @RequestMapping(value = "/api/useraudioitem/{username}", method = RequestMethod.GET)
    public List<UserAudioItem> getUserAudioItemByUsername(@PathVariable("username") String username) throws JsonProcessingException {
        if (userLoginService.getUser() == null) {
            return null;
        }

        String loginUsername = userLoginService.getUser().getUsername();
        if (loginUsername.equals(username)) {
            Instant instant = Instant.now();
            List<UserAudioItem> items = userAudioitemRepository.findByExpiredOrExpiredGreaterThan(0, instant.getEpochSecond());

            List<UserAudioItem> usernameItems = items.stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
            usernameItems.forEach(item -> {
                logger.info("audio:" + item.getDescription() + "-" + item.getExpired() + "-" + item.getUsername());
            });
            return (usernameItems);
        } else {
            return null;
        }


    }

    @RequestMapping(value = "/api/useraudioitem", method = RequestMethod.PUT)
    public String updateUserItem(@RequestBody UserAudioItem item) throws JsonProcessingException {

        item.getPlaytime().forEach((k, v) -> {
            logger.info(item.getDescription() + ":" + k + "-" + v);
        });
        userAudioitemService.update(item);

        return mapper.writeValueAsString(item);
    }


}
