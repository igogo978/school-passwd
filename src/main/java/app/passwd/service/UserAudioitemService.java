package app.passwd.service;

import app.passwd.model.SyncUseritem;
import app.passwd.model.UserAudioItem;
import app.passwd.model.UserItem;
import app.passwd.repository.SyncUseritemRepository;
import app.passwd.repository.UserAudioitemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAudioitemService {

    private final Logger logger = LoggerFactory.getLogger(UserAudioitemService.class);

    @Autowired
    UserAudioitemRepository userAudioitemRepository;

    @Autowired
    UseritemUtils useritemUtils;

    @Autowired
    SyncUseritemRepository syncUseritemRepository;

    public String getBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    public void saveAudio(String username, MultipartFile file) throws IOException, InterruptedException {

        String encode = getBase64(file);
        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();

        String prefix = (String.format("data:%s;base64,", file.getContentType()));

        UserAudioItem userAudioItem = new UserAudioItem(prefix, username, "audio", timestamp, 0, file.getOriginalFilename());
        userAudioItem.setContent(normalize(encode));

        userAudioitemRepository.save(userAudioItem);

    }

    public void disable(UserAudioItem item) {

        Instant instant = Instant.now();
        item.setExpired(instant.getEpochSecond());
        userAudioitemRepository.save(item);

        //update sync item timestamp for target is audio
        List<SyncUseritem> items = syncUseritemRepository.findByTarget("audio");
        items.forEach(syncUseritem -> {
            syncUseritem.setTimestamp(0);
            syncUseritemRepository.save(syncUseritem);

        });
    }

    public String normalize(String encodes) throws IOException, InterruptedException {
        String normalizeDecodes = "";
        //working dir
        File dir = new File("/tmp/audio/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //base64 content decode to file
        byte[] bytes = Base64.getDecoder().decode(encodes);
        String normfilename = dir + "norm.mp3";
        Path rawfilePath = Paths.get(dir + "/" + "raw.mp3");
        Files.write(rawfilePath, bytes);

        String[] command = {"/usr/bin/sox", "--norm=-2", rawfilePath.toString(), normfilename};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(dir);

        int intExitCode = processBuilder.start().waitFor();
        if (intExitCode == 0) {
            bytes = Files.readAllBytes(new File(normfilename).toPath());
            normalizeDecodes = Base64.getEncoder().encodeToString(bytes);
        }
        logger.info("audio normalized");

        return normalizeDecodes;

    }

    public void udpateExipredDay(UserAudioItem item) {

        Instant instant = Instant.now();
        Instant expiredday = instant.plus(item.getExpired(), ChronoUnit.DAYS);

        item.setTimestamp(instant.getEpochSecond());
        item.setExpired(expiredday.getEpochSecond());

        userAudioitemRepository.save(item);
        //syncuseritem set timestamp 0
        List<SyncUseritem> syncUseritems = syncUseritemRepository.findByTarget("audio");
        syncUseritems.forEach(syncUseritem -> {
            syncUseritem.setTimestamp(0);
            syncUseritemRepository.save(syncUseritem);
        });

    }


    public void update(UserAudioItem item) {

        logger.info("update item expred time: " + item.getUsername() + "-" + item.getDescription() + "-expired day: " + item.getExpired());
        Instant instant = Instant.now();
        if (item.getExpired() == -1) {
            disable(item);
        } else {
            udpateExipredDay(item);
        }

    }


    public UserAudioItem getUserAudioItemById(String id) {
        return userAudioitemRepository.findById(id).get();
    }


    public List<UserItem> getUseritemsEnabled(long now) {
        List<UserAudioItem> audioItems = new ArrayList<>();
        audioItems = userAudioitemRepository.findByExpiredGreaterThan(now);
        List<UserItem> items = new ArrayList<>();
        audioItems.forEach(audioItem -> {
            audioItem.setContent("");
            UserItem item = (UserItem) audioItem;
            items.add(item);
        });

        return items;
    }

    public List<UserAudioItem> getExpiredItem(int days) {

        Instant instant = Instant.now();
        Instant past = instant.minus(days, ChronoUnit.DAYS);
        List<UserAudioItem> items = userAudioitemRepository.findByExpiredLessThan(past.getEpochSecond()).stream()
                .filter(item -> item.getExpired() != 0).collect(Collectors.toList());

        items.forEach(userAudioItem -> {
            logger.info("past item: " + userAudioItem.getDescription());
            userAudioitemRepository.delete(userAudioItem);
        });

//        return userAudioitemRepository.findByExpiredLessThan(past.getEpochSecond()).stream().filter(item -> item.getExpired() != 0).collect(Collectors.toList());
        return items;
    }


}
