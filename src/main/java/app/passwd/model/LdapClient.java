package app.passwd.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Entity
public class LdapClient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer sn;
    private String basedn;
    private String ldapserver;
    private String ldapport;
    private String rootdn;
    private String passwd;
    private Integer uidNumber;
    private String sid;
    private String objectclass;

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    @ElementCollection
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return roles;
    }

    public String getBasedn() {
        return basedn;
    }

    public void setBasedn(String basedn) {
        this.basedn = basedn;
    }

    public String getLdapserver() {
        return ldapserver;
    }

    public void setLdapserver(String ldapserver) {
        this.ldapserver = ldapserver;
    }

    public String getLdapport() {
        return ldapport;
    }

    public void setLdapport(String ldapport) {
        this.ldapport = ldapport;
    }

    public String getRootdn() {
        return rootdn;
    }

    public void setRootdn(String rootdn) {
        this.rootdn = rootdn;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Integer getUidNumber() {
        return uidNumber;
    }

    public void setUidNumber(Integer uidNumber) {
        this.uidNumber = uidNumber;
    }

    public String getObjectclass() {
        return objectclass;
    }

    public void setObjectclass(String objectclass) {
        this.objectclass = objectclass;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
