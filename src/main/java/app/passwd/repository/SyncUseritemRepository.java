package app.passwd.repository;

import app.passwd.model.SyncUseritem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SyncUseritemRepository extends MongoRepository<SyncUseritem, String> {

    SyncUseritem findByRunnerAndTarget(String runner,String target);
    List<SyncUseritem> findByTarget(String target);
}
