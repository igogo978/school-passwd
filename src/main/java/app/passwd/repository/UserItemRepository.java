package app.passwd.repository;

import app.passwd.model.UserItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserItemRepository extends MongoRepository<UserItem, String> {

    List<UserItem> findByUsername(String username);

    List<UserItem> findByExpired(long now);

    List<UserItem> findByExpiredGreaterThan(long now);

    List<UserItem> findByTimestampLessThan(long now);

    List<UserItem> findByExpiredOrExpiredGreaterThanAndUsername(long zero, long now, String username);

    List<UserItem> findByExpiredOrExpiredGreaterThan(long zero, long now);

}
