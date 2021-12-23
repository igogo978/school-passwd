package app.passwd.model;

import java.util.HashMap;
import java.util.Map;

public class UserAudioItem extends UserItem {
    Map<String, String> playtime = new HashMap<>();
    private String content;

    public UserAudioItem(String prefix, String username, String type, long timestamp, long expired, String description) {
        super(prefix, username, type, timestamp, expired, description);
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getPlaytime() {
        return playtime;
    }
}
