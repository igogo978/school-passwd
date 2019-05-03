package app.passwd;

import app.passwd.ldap.model.User;
import app.passwd.service.LdapTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.ldap.LdapName;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswdApplicationTests {

//	@Autowired
//	LdapTools ldapTools;

	@Test
	public void contextLoads() {
//		String username = "107-107007";
//
//		if (ldapTools.isUserExist(username)) {
//			User user1 = ldapTools.findByUid(username);
//			LdapName ldapName = LdapUtils.newLdapName(user1.getDn());
//			ldapName.getRdns().forEach(rdn -> System.out.println(rdn.toString()));
//		}
	}

}
