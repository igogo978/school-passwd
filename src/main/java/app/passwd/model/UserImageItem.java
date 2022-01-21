package app.passwd.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserImageItem extends UserItem{

    private String content;

    public UserImageItem(String prefix, String username, String type, long timestamp, long expired, String description) {
        super(prefix, username, type, timestamp, expired, description);
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
