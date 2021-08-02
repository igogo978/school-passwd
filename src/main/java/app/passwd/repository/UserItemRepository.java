package app.passwd.repository;

import app.passwd.model.UserItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserItemRepository extends MongoRepository<UserItem,String> {

}
