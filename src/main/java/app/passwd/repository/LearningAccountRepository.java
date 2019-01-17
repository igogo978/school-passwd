package app.passwd.repository;

import app.passwd.model.LearningAccount;
import org.springframework.data.repository.CrudRepository;

public interface LearningAccountRepository extends CrudRepository<LearningAccount, Integer> {
   LearningAccount findByClassnameAndSeatno(String classname, String seatno);
}
