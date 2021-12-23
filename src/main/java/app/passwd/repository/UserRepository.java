package app.passwd.repository;

import app.passwd.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {

    public Optional<User> findByUsername(String username);
    public void deleteByUsername(String username);
}
