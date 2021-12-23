package app.passwd.service;

import app.passwd.model.SyncUseritem;
import app.passwd.repository.SyncUseritemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncUseritemService {

    private final Logger logger = LoggerFactory.getLogger(SyncUseritemService.class);

    @Autowired
    SyncUseritemRepository syncUseritemRepository;
    public void save(SyncUseritem item) {
        SyncUseritem syncUseritem = syncUseritemRepository.findByRunnerAndTarget(item.getRunner(),item.getTarget());
       syncUseritemRepository.save(item);
    }

    public List<SyncUseritem> findByTarget(String target) {
        return syncUseritemRepository.findByTarget(target);
    }

    public SyncUseritem get(String runner, String target) {
        return syncUseritemRepository.findByRunnerAndTarget(runner,target);
    }

    public List<SyncUseritem> get() {
        return syncUseritemRepository.findAll();
    }
}
