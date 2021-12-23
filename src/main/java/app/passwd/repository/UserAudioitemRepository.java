package app.passwd.repository;

import app.passwd.model.UserAudioItem;
import app.passwd.model.UserItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserAudioitemRepository extends MongoRepository<UserAudioItem, String> {

//    List<UserItem> findByUsername(String username);
//
//    List<UserItem> findByExpired(long now);
//
//    List<UserItem> findByExpiredGreaterThan(long now);
//    List<UserItem> findByExpiredGreaterThanOrderByTimestampDesc(long now);
//
//    List<UserItem> findByTimestampLessThan(long now);

    List<UserItem> findByExpiredGreaterThanOrderByTimestampDesc(long now);
    List<UserItem> findByExpiredGreaterThan(long now);
//    List<UserAudioItem> findByExpiredGreaterThanOrderByTimestampDesc(long now);
    List<UserAudioItem> findByExpiredOrExpiredGreaterThan(long zero, long now);

    List<UserAudioItem> findByExpiredLessThan(long past);
//    Optional<UserAudioItem> findById(String id);

}
