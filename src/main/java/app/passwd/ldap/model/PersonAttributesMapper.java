package app.passwd.ldap.model;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class PersonAttributesMapper implements AttributesMapper<User> {
    @Override
    public User mapFromAttributes(Attributes attributes) throws NamingException {
        User user = new User();

        user.setCn((String) attributes.get("cn").get());
        user.setUid((String) attributes.get("uid").get());
        user.setUidNumber((String) attributes.get("uidNumber").get());
        user.setHomeDirectory((String) attributes.get("homeDirectory").get());

        return user;
    }
}
