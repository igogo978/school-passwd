package app.passwd.repository;

import app.passwd.model.UserImageItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserImageItemRepository extends MongoRepository<UserImageItem, String> {

    List<UserImageItem> findByUsername(String username);

    List<UserImageItem> findByExpired(long now);

    List<UserImageItem> findByExpiredGreaterThan(long now);
    List<UserImageItem> findByExpiredGreaterThanOrderByTimestampDesc(long now);

    List<UserImageItem> findByTimestampLessThan(long now);

    List<UserImageItem> findByExpiredOrExpiredGreaterThan(long zero, long now);

    List<UserImageItem> findByExpiredLessThan(long past);

}
