package app.passwd.ldap.model;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

public class ADUser {

    @Id
    private Name id;


    private @Attribute(name = "cn") String cn;
    private @Attribute(name = "displayName") String displayName;
    private @Attribute(name = "userAccountControl") String userAccountControl;
    private @Attribute(name = "sAMAccountName") String sAMAccountName;
    private @Attribute(name = "userPrincipalName") String userPrincipalName;
    private @Attribute(name = "distinguishedName") String distinguishedName;

    public ADUser() {
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getUserAccountControl() {
        return userAccountControl;
    }

    public void setUserAccountControl(String userAccountControl) {
        this.userAccountControl = userAccountControl;
    }

    public String getsAMAccountName() {
        return sAMAccountName;
    }

    public void setsAMAccountName(String sAMAccountName) {
        this.sAMAccountName = sAMAccountName;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }
}
