package app.passwd.model;

import javax.persistence.Embeddable;

@Embeddable
public class Role {
    private String role;
    private String ou;



    public Role() {
    }

    public Role(String role, String ou) {
        this.role = role;
        this.ou = ou;
    }

    public String getOu() {
        return ou;
    }

    public String getRole() {
        return role;
    }
}
