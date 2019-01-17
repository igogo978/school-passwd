package app.passwd.repository;

import app.passwd.model.LdapClient;
import org.springframework.data.repository.CrudRepository;

public interface LdapRepository extends CrudRepository<LdapClient, Integer> {
    LdapClient findBySn(int sn);
}
