package app.passwd.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class UserItem {
    @Id
    private String id;

    private String prefix;
    private final String username;
    private String type;
    private long timestamp;
    private long expired;
    private String description;

    Map<String, String> playtime = new HashMap<>();

    public UserItem(String prefix, String username, String type, long timestamp, long expired, String description) {
        this.prefix = prefix;
        this.username = username;
        this.type = type;
        this.timestamp = timestamp;
        this.expired = expired;
        this.description = description;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getPlaytime() {
        return playtime;
    }
}
