package app.passwd.ldap.model;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Entry(objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" })
public class User {

    @Id
    private Name dn;

    private @Attribute(name = "uid") String uid;
    private @Attribute(name = "cn") String cn;
    private @Attribute(name = "uidNumber") String uidNumber;
    private @Attribute(name = "homeDirectory") String homeDirectory;


    public User() {
    }

    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getUid() {
        return uid;
    }

    public String getCn() {
        return cn;
    }

    public String getUidNumber() {
        return uidNumber;
    }

    public void setUidNumber(String uidNumber) {
        this.uidNumber = uidNumber;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }


}
