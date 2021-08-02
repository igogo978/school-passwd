package app.passwd.model;

import org.bson.types.Binary;

import javax.persistence.Id;

public class UserItem {
    @Id
    private  String id;

    private  String username;
    private String type;
    private String content;

    public UserItem(String username, String type, String content) {
        this.username = username;
        this.type = type;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
