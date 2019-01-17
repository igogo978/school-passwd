package app.passwd.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LearningAccount {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String classname;
    private String seatno;
    private String name;
    private String learningaccount;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSeatno() {
        return seatno;
    }

    public void setSeatno(String seatno) {
        this.seatno = seatno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLearningaccount() {
        return learningaccount;
    }

    public void setLearningaccount(String learningaccount) {
        this.learningaccount = learningaccount;
    }
}
