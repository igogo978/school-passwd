package app.passwd.api;

import app.passwd.model.UserImageItem;
import app.passwd.repository.UserImageItemRepository;
import app.passwd.service.UserLoginService;
import app.passwd.service.UseritemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserItemAPIController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    UserImageItemRepository userImageItemRepository;

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    UseritemService useritemService;


    @RequestMapping(value = "/api/useritem/video/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream video = useritemService.getVideo(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.set("Accept-Ranges", "bytes");
        headers.set("Expires", "0");
        headers.set("Cache-Control", "no-cache, no-store");
        headers.set("Connection", "keep-alive");
        headers.set("Content-Transfer-Encoding", "binary");
        return new ResponseEntity<>(new InputStreamResource(video), headers, HttpStatus.OK);

    }


    @RequestMapping(value = "/api/useritem", method = RequestMethod.GET)
    public String getUserItemsEnabled() throws JsonProcessingException {
        Instant instant = Instant.now();
        List<UserImageItem> items = useritemService.getUseritemsEnabled(instant.getEpochSecond());
        return mapper.writeValueAsString(items);
    }


    @RequestMapping(value = "/api/useritem/{username}", method = RequestMethod.GET)
    public String getUserItem(@PathVariable("username") String username) throws JsonProcessingException {


        if (userLoginService.getUser() == null) {
            return "";
        }
        String loginUsername = userLoginService.getUser().getUsername();
        if (loginUsername.equals(username)) {
            Instant instant = Instant.now();
            List<UserImageItem> items = userImageItemRepository.findByExpiredOrExpiredGreaterThan(0, instant.getEpochSecond());
//            items.forEach(userItem -> logger.info(userItem.getUsername()+"-"+userItem.getExpired()));

            List<UserImageItem> usernameItems = items.stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
            return mapper.writeValueAsString(usernameItems);
        } else {
            return "";
        }

    }

    @RequestMapping(value = "/api/useritem", method = RequestMethod.PUT)
    public String updateUserItem(@RequestBody UserImageItem item) {

        useritemService.update(item);
        return String.valueOf(item.getExpired());
    }


    @RequestMapping(value = "/api/useritem/expired", method = RequestMethod.GET)
    public List<UserImageItem> getUseritemExpired() {
        List<UserImageItem> items = new ArrayList<>();
        items = useritemService.getExpiredItem(0);
        return items;
    }
}
