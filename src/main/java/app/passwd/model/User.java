package app.passwd.model;

public class User {
    private String school_no;
    private String username;
    private String role;
    private String name;
    private String edu_key;

    private String learningaccount;

    public String getLearningaccount() {
        return learningaccount;
    }

    public User(String school_no, String username, String role, String name, String edu_key) {
        this.school_no = school_no;
        this.username = username;
        this.role = role;
        this.name = name;
        this.edu_key = edu_key;

    }

    public String getSchool_no() {
        return school_no;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEdu_key() {
        return edu_key;
    }
}
