package app.passwd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;


public class User  {

    @Id
    @Indexed(unique = true)
    private String username;

    private String school_no;
    private List<String> roles = new ArrayList<>();
    private String name;
    private String edu_key;
    private int quota = 0;
    private boolean status;


    public User() {
    }

    public User(String school_no, String username, List<String> roles, String name, String edu_key) {
        this.school_no = school_no;
        this.username = username;
        this.roles = roles;
        this.name = name;
        this.edu_key = edu_key;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getSchool_no() {
        return school_no;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEdu_key() {
        return edu_key;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
