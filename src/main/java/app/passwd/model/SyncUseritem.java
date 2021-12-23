package app.passwd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class SyncUseritem {

    @Id
    @Indexed(unique = true)
    private  String id;

    private String runner;

    private String target;
    private long timestamp;

    public SyncUseritem() {
    }

    public SyncUseritem(String runner, String target, long timestamp) {
        this.runner = runner;
        this.target = target;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRunner() {
        return runner;
    }

    public void setRunner(String runner) {
        this.runner = runner;
    }

    public String getTarget() {
        return target;
    }
}
