package app.passwd.model;

import javax.persistence.Id;

public class UserItem {
    @Id
    private String id;

    private final String username;
    private String type;
    private String content;
    private long timestamp;
    private long expired;

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }


    public UserItem(String username, String type, String content, long timestamp, long expired) {
        this.username = username;
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
        this.expired = expired;
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
