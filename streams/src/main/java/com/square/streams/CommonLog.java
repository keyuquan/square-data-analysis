package com.square.streams;

public class CommonLog {
    private String app_code;
    private String owner_code;
    private String event_code;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp_code() {
        return app_code;
    }

    public void setApp_code(String app_code) {
        this.app_code = app_code;
    }

    public String getOwner_code() {
        return owner_code;
    }

    public void setOwner_code(String owner_code) {
        this.owner_code = owner_code;
    }

    public String getEvent_code() {
        return event_code;
    }

    public void setEvent_code(String event_code) {
        this.event_code = event_code;
    }
}
