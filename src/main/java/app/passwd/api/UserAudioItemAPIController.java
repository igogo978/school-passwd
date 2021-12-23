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
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserAudioItemAPIController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    UserAudioitemRepository userAudioitemRepository;

    @Autowired
    UserAudioitemService userAudioitemService;

    @Autowired
    UserLoginService userLoginService;


    @RequestMapping(value = "/api/useraudioitem", method = RequestMethod.GET)
    public String getUserAudioItemEnabled() throws JsonProcessingException {
        Instant instant = Instant.now();
        List<UserItem> items = userAudioitemService.getUseritemsEnabled(instant.getEpochSecond());
//        logger.info(mapper.writeValueAsString(items.get(0)));
        return mapper.writeValueAsString(items);
    }

    @RequestMapping(value = "/api/useraudioitem/id/{id}", method = RequestMethod.GET)
    public String getUserAudioItemByID(@PathVariable("id") String id) throws JsonProcessingException {
      return mapper.writeValueAsString(userAudioitemService.getUserAudioItemById(id));
    }


    @RequestMapping(value = "/api/useraudioitem/{username}", method = RequestMethod.GET)
    public String getUserAudioItemByUsername(@PathVariable("username") String username) throws JsonProcessingException {
        if (userLoginService.getUser() == null) {
            return "";
        }

        String loginUsername = userLoginService.getUser().getUsername();
        if (loginUsername.equals(username)) {
            Instant instant = Instant.now();
            List<UserAudioItem> items = userAudioitemRepository.findByExpiredOrExpiredGreaterThan(0, instant.getEpochSecond());
//            logger.info("audio request: " + items.size());
//            items.forEach(userItem -> logger.info(userItem.getUsername() + "-" + userItem.getExpired()));


            List<UserAudioItem> usernameItems = items.stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
            usernameItems.forEach(item -> {
                logger.info("audio:" + item.getDescription() + "-" + item.getExpired() + "-" + item.getUsername());
            });
            return mapper.writeValueAsString(usernameItems);
        } else {
            return "";
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
