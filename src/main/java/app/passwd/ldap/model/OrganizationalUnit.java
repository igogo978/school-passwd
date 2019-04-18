package app.passwd.ldap.model;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

public class OrganizationalUnit {
    @Id
    private Name id;

    private @Attribute(name = "name") String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
