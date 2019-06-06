package app.passwd.model;

public class User {
    private String school_no;
    private String username;
    private String adusername;
    private String role;
    private String name;
    private String edu_key;
//    private String learningaccount;

//    public String getLearningaccount() {
//        return learningaccount;
//    }


    public User() {
    }

    public User(String school_no, String username, String adusername, String role, String name, String edu_key) {
        this.school_no = school_no;
        this.username = username;
        this.adusername = adusername;
        this.role = role;
        this.name = name;
        this.edu_key = edu_key;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAdusername(String adusername) {
        this.adusername = adusername;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAdusername() {
        return adusername;
    }
}
