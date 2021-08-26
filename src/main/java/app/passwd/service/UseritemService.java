package app.passwd.service;

import app.passwd.model.UserItem;
import app.passwd.repository.UserItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

@Service
public class UseritemService {

    @Autowired
    UserItemRepository userItemRepository;

    public String getBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    public void save(String username, MultipartFile file) throws IOException {
        String encode = getBase64(file);

        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();
        UserItem userItem = new UserItem(username, "png", encode, timestamp, (long) 0);

        userItemRepository.save(userItem);
    }

    public void update(UserItem item) {
        userItemRepository.save(item);
    }
}
