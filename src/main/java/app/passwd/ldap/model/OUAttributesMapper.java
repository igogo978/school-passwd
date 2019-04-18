package app.passwd.ldap.model;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class OUAttributesMapper implements AttributesMapper<OrganizationalUnit> {
    @Override
    public OrganizationalUnit mapFromAttributes(Attributes attributes) throws NamingException {
        OrganizationalUnit ou = new OrganizationalUnit();
        ou.setName((String) attributes.get("name").get());
        return ou;
    }
}
