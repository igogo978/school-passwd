package app.passwd.model;

import javax.persistence.Embeddable;

@Embeddable
public class Role {
    private String ou;
    private Integer gid;
    private String home;


    public Role() {
    }

    public Role(String ou, Integer gid, String home) {
        this.ou = ou;
        this.gid = gid;
        this.home = home;
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
