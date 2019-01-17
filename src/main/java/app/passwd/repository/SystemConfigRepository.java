package app.passwd.repository;

import app.passwd.model.SystemConfig;
import org.springframework.data.repository.CrudRepository;

public interface SystemConfigRepository extends CrudRepository<SystemConfig, Integer> {
    SystemConfig findBySn(int sn);
}
