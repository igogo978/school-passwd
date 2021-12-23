package app.passwd.service;

import app.passwd.model.User;
import app.passwd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void update(User user) {
        userRepository.save(user);

    }


}
