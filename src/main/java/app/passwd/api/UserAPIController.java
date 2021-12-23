package app.passwd.api;

import app.passwd.model.User;
import app.passwd.repository.UserRepository;
import app.passwd.service.UserLoginService;
import app.passwd.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UserAPIController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    UserService userService;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/api/user/quota/{quota}")
    public void setUserQuota(@PathVariable int quota) {
        String username = userLoginService.getUser().getUsername();
    }


    @PutMapping("/api/user")
    public String updateUser(@RequestBody User user) throws JsonProcessingException {
        String content = "";

        //check if has admin role
        User admin = userLoginService.getUser();

        if (admin.getRoles().stream().anyMatch(role->role.equals("admin"))) {
            userService.update(user);
            content = mapper.writeValueAsString(user);

        }

        return content;

    }


    @GetMapping("/api/user")
    public String getUsers() throws JsonProcessingException {
        String users = "";
//        String username = userLoginService.getUser().getUsername();
//        boolean isAdmin = userRepository.findByUsername(username).get().getRoles().stream().anyMatch( role -> role.equals("admin"));
//        if(isAdmin) {
//            users = mapper.writeValueAsString(userRepository.findAll());
//        }
        users = mapper.writeValueAsString(userRepository.findAll());
        return users;
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> setUserLogout() {
        userLoginService.setUserLogout();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();

    }
}
