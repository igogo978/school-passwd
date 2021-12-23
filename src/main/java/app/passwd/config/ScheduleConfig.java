package app.passwd.config;

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

@Configuration
@EnableScheduling
public class ScheduleConfig {
    private final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    @Autowired
    UseritemService useritemService;

    @Autowired
    UserAudioitemService userAudioitemService;

//    @Scheduled(cron = "3 */10 * * * ?", zone = "Asia/Taipei")
//    public void syncUserItem() throws IOException {
//        Instant instant = Instant.now();
//        String path = "/home/public/led/設定/傳送門/";
//
//        useritemService.getUseritemsAndSaveFile(instant.getEpochSecond(), path);
//    }


    @Scheduled(cron = "1 50 8 13 3,9 ?", zone = "Asia/Taipei")
    public void clean() throws IOException {
        //delete  video in gridFs
        useritemService.getExpiredItem(181);
        userAudioitemService.getExpiredItem(181);
    }
}
