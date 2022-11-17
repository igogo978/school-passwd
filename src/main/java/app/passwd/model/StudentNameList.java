package app.passwd.model;

import java.util.ArrayList;
import java.util.List;

public class StudentNameList {
    private String kind;
    private String class_no;
    private List item_key = new ArrayList();

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getClass_no() {
        return class_no;
    }

    public void setClass_no(String class_no) {
        this.class_no = class_no;
    }

    public List getItem_key() {
        return item_key;
    }
}
