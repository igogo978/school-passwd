package app.passwd.service;

import app.passwd.model.SyncUseritem;
import app.passwd.model.UserImageItem;
import app.passwd.repository.SyncUseritemRepository;
import app.passwd.repository.UserImageItemRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UseritemService {

    private final Logger logger = LoggerFactory.getLogger(UseritemService.class);


    @Autowired
    UserImageItemRepository userImageItemRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

//    @Autowired
//    UseritemUtils useritemUtils;

    @Autowired
    SyncUseritemService syncUseritemService;

    @Autowired
    SyncUseritemRepository syncUseritemRepository;

    public String getBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    //    https://stackoverflow.com/questions/49153910/how-to-get-a-binary-stream-by-gridfs-objectid-with-spring-data-mongodb
    public InputStream getVideo(String id) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        GridFsResource resource = gridFsTemplate.getResource(file);
        return resource.getInputStream();

    }

    public void deleteVideo(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    public void saveImage(String username, MultipartFile file) throws IOException {
//        String encode = "data:image/png;base64," + getBase64(file);

        String encode = getBase64(file);
        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();

        String prefix = (String.format("data:%s;base64,", file.getContentType()));
        UserImageItem userItem = new UserImageItem(prefix, username, "image", timestamp, 0, file.getOriginalFilename().toLowerCase(Locale.ROOT).replace("jpeg", "jpg"));
        userItem.setContent(encode);
        userItem.setDescription(file.getOriginalFilename());
        userImageItemRepository.save(userItem);
    }

    public void saveVideo(String username, MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("username", username);

        String id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metaData).toString();

        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();
        UserImageItem userItem = new UserImageItem(file.getContentType(), username, "video", timestamp, 0, file.getOriginalFilename());
        userItem.setContent(id);
        userImageItemRepository.save(userItem);


    }

    public void disable(UserImageItem item) {

        Instant instant = Instant.now();
        item.setExpired(instant.getEpochSecond());

        logger.info("disable now: " + item.getUsername() + "-" + item.getDescription());
        userImageItemRepository.save(item);


    }

    public void udpateExipredDay(UserImageItem item) {

        Instant instant = Instant.now();
        Instant expiredday = instant.plus(item.getExpired(), ChronoUnit.DAYS);
        item.setExpired(expiredday.getEpochSecond());
        logger.info("update expired day: " + item.getUsername() + "-" + item.getDescription());
        userImageItemRepository.save(item);
    }


    public void update(UserImageItem item) {

        //update item and reset sync timestamp
        Instant instant = Instant.now();
        if (item.getExpired() == -1) {
            disable(item);
        } else {
            udpateExipredDay(item);
        }
        //update sync item timestamp for target is led
        List<SyncUseritem> items = syncUseritemRepository.findByTarget("led");
        items.forEach(syncUseritem -> {
            syncUseritem.setTimestamp(0);
            syncUseritemRepository.save(syncUseritem);

        });


    }


    public List<UserImageItem> getUseritemsEnabled(long now) {
        return userImageItemRepository.findByExpiredGreaterThanOrderByTimestampDesc(now);
    }

    public List<UserImageItem> getExpiredItem(int days) {
        Instant instant = Instant.now();
        Instant past = instant.minus(days, ChronoUnit.DAYS);
        List<UserImageItem> items = userImageItemRepository.findByExpiredLessThan(past.getEpochSecond()).stream().filter(item -> item.getExpired() != 0).collect(Collectors.toList());
        logger.info("past items: " + items.size());
        items.forEach(item -> {
            ZonedDateTime expired = Instant.ofEpochSecond(item.getExpired()).atZone(ZoneId.of("Asia/Taipei"));
            String expiredHR = (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(expired));
//
            logger.info(expiredHR + "-" + item.getUsername() + "-" + item.getType() + "-" + item.getDescription());
        });

        return userImageItemRepository.findByExpiredLessThan(past.getEpochSecond()).stream().filter(item -> item.getExpired() != 0).collect(Collectors.toList());

    }


//    public void getUseritemsAndSaveFile(long now, String path) throws IOException {
//        List<UserImageItem> items = userImageItemRepository.findByExpiredGreaterThanOrderByTimestampDesc(now);
//
//
//        //check update timestamp
//        SyncUseritem syncUseritem = syncUseritemRepository.findByRunnerAndTarget("web", "led");
////        logger.info("web runner: db data =" + syncUseritem.getTimestamp() + ":" + items.get(0).getTimestamp());
//
//        if (items.get(0).getTimestamp() != syncUseritem.getTimestamp()) {
//
//            //empty path
//            FileSystemUtils.deleteRecursively(Paths.get(path));
//            new File(path).mkdir();
//
//            AtomicInteger index = new AtomicInteger(1);
//            items.forEach(item -> {
//                if (item.getType().equals("image")) {
//                    try {
//                        useritemUtils.save(index, item, path);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                if (item.getType().equals("video")) {
//                    try {
//                        InputStream inputStream = getVideo(item.getContent());
//                        useritemUtils.save(index, item, inputStream, path);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                index.incrementAndGet();
//            });
//            syncUseritem.setTimestamp(items.get(0).getTimestamp());
//            syncUseritemRepository.save(syncUseritem);
//        }
//
//    }

    public Map<String, Long> count(long now) {
        Map<String, Long> itemcounts = new HashMap<>();
        long count;
        List<UserImageItem> items = userImageItemRepository.findByExpiredGreaterThanOrderByTimestampDesc(now);
        count = items.stream().filter(item -> item.getDescription().toLowerCase(Locale.ROOT).endsWith("jpg")).count();
        itemcounts.put("jpg", count);

        count = items.stream().filter(item -> item.getDescription().toLowerCase(Locale.ROOT).endsWith("png")).count();
        itemcounts.put("png", count);

        count = items.stream().filter(item -> item.getDescription().toLowerCase(Locale.ROOT).endsWith("mp4")).count();
        itemcounts.put("mp4", count);

        return itemcounts;
    }
}