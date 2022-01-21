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


//    @Scheduled(cron = "1 50 8 13 3,9 ?", zone = "Asia/Taipei")
    @Scheduled(cron = "12 09 16 * * ?", zone = "Asia/Taipei")
    public void clean() throws IOException {
        //delete  video in gridFs
        useritemService.getExpiredItem(181);
        userAudioitemService.getExpiredItem(101);
    }
}
