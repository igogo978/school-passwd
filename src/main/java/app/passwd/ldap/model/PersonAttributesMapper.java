package app.passwd.ldap.model;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class PersonAttributesMapper implements AttributesMapper<ADUser> {
    @Override
    public ADUser mapFromAttributes(Attributes attributes) throws NamingException {
        ADUser ADUser = new ADUser();
        ADUser.setCn((String) attributes.get("cn").get());
        ADUser.setDisplayName((String) attributes.get("displayName").get());
        ADUser.setsAMAccountName((String) attributes.get("sAMAccountName").get());
        ADUser.setUserPrincipalName((String) attributes.get("userPrincipalName").get());
        ADUser.setDistinguishedName((String) attributes.get("distinguishedName").get());

        return ADUser;
    }
}
