package app.passwd.ldap.model;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class PersonAttributesMapper implements AttributesMapper<ADUser> {
    @Override
    public ADUser mapFromAttributes(Attributes attributes) throws NamingException {
        ADUser ADUser = new ADUser();
        ADUser.setCn((String) attributes.get("cn").get());

        return ADUser;
    }
}
