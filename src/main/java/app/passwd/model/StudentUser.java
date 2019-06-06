package app.passwd.model;

public class StudentUser extends User {

    public StudentUser() {
    }

    private String password;
    private String classno;

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getClassno() {
        return classno;
    }

    public void setClassno(String classno) {
        this.classno = classno;
    }


}
