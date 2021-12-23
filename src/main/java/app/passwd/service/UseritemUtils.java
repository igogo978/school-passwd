package app.passwd.service;

import app.passwd.model.UserImageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UseritemUtils {

    private final Logger logger = LoggerFactory.getLogger(UseritemUtils.class);

    //jpg base64
    public void save(AtomicInteger index, UserImageItem item, String path) throws IOException {
        String filename = path + index.get() + "-" + item.getUsername() + "-" + item.getDescription();
        byte[] bytes = Base64.getDecoder().decode(item.getContent());
        if (new File(path).exists()) {
            Files.write(Path.of(filename), bytes);
            logger.info("copy successfully" + filename);
        }

    }
    public void save(UserImageItem item, String path) throws IOException {
        String filename = path + item.getUsername() + "-" + item.getDescription();
        byte[] bytes = Base64.getDecoder().decode(item.getContent());
        if (new File(path).exists()) {
            Files.write(Path.of(filename), bytes);
            logger.info("copy successfully" + filename);
        }

    }





    //video inputstream
    public void save(AtomicInteger index, UserImageItem item, InputStream inputStream, String path) throws IOException {

        String filename = path + index.get() + "-" + item.getUsername() + "-" + item.getDescription();
        if (new File(path).exists()) {
            Files.copy(inputStream,
                    new File(filename).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            logger.info("copy successfully" + filename);
        }
    }

}
