package app.passwd.config;

import app.passwd.model.UserAudioItem;
import app.passwd.model.UserImageItem;
import app.passwd.model.UserItem;
import app.passwd.service.UserAudioitemService;
import app.passwd.service.UseritemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    private final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    @Autowired
    UseritemService useritemService;

    @Autowired
    UserAudioitemService userAudioitemService;


    //    @Scheduled(cron = "1 50 8 13 3,9 ?", zone = "Asia/Taipei")
    @Scheduled(cron = "12 15 09 * * ?", zone = "Asia/Taipei")
    public void clean() throws IOException {
        //delete  video in gridFs
        List<UserImageItem> userImageItems = useritemService.getExpiredItem(181);
        List<UserAudioItem> userAudioItems = userAudioitemService.getExpiredItem(101);

        userImageItems.forEach(userImageItem -> useritemService.delete(userImageItem));
        userAudioItems.forEach(userAudioItem -> userAudioitemService.delete(userAudioItem));


    }
}
