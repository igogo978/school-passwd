package app.passwd.model;

import javax.persistence.Embeddable;

@Embeddable
public class Role {
    private String role;
    private String ou;
    private Integer gid;
    private String home;


    public Role() {
    }

    public Role(String role, String ou, Integer gid, String home) {
        this.role = role;
        this.ou = ou;
        this.gid = gid;
        this.home = home;
    }

    public String getRole() {
        return role;
    }

    public String getOu() {
        return ou;
    }

    public Integer getGid() {
        return gid;
    }

    public String getHome() {
        return home;
    }
}
