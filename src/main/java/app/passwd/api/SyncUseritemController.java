package app.passwd.api;

import app.passwd.model.SyncUseritem;
import app.passwd.service.SyncUseritemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SyncUseritemController {

    private final Logger logger = LoggerFactory.getLogger(SyncUseritemController.class);

    @Autowired
    @Lazy
    SyncUseritemService syncUseritemService;

    @GetMapping("/api/syncuseritem/{runner}/{target}")
    public SyncUseritem getSyncUseritemRunnerAndTarget(@PathVariable String runner, @PathVariable String target) {
        SyncUseritem syncUseritem = syncUseritemService.get(runner, target);
        return syncUseritem;
    }


    @GetMapping("/api/syncuseritem")
    public List<SyncUseritem> getSyncUseritem() {
        return syncUseritemService.get();
    }


    @PutMapping("/api/syncuseritem")
    public void updateSyncTimestamp(@RequestBody SyncUseritem syncUseritem, @RequestHeader("Authorization") String token) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String validtoken = String.format("Bearer 123456");

        if (validtoken.equals(token)) {
//            logger.info(mapper.writeValueAsString(syncUseritem));
            syncUseritemService.save(syncUseritem);
        }

    }

}
