package app.passwd.ldap.model;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

public class OrganizationalUnit {
    @Id
    private Name id;

    private @Attribute(name = "ou") String ou;

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }
}
