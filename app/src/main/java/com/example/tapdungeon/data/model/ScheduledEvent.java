package com.example.tapdungeon.data.model;

import java.util.List;

public class ScheduledEvent implements InboxItemInterface {

    private String id;
    private String title;
    private String description;
    private long timestamp;
    private List<String> sentToPlayers;
    private String type; // stored as SCHEDULED_EVENT

    public ScheduledEvent(String id, String title, String description, long timestamp, List<String> sentToPlayers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.sentToPlayers = sentToPlayers;
        this.type = InboxItemType.SCHEDULED_EVENT.name();
    }

    @Override
    public InboxItemType getItemType() {
        return InboxItemType.valueOf(type);
    }

    @Override
    public long getTimestamp() { return timestamp; }

    @Override
    public String getId() { return id; }

    @Override
    public List<String> getSentToPlayers() { return sentToPlayers; }

    // Firebase setters/getters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setSentToPlayers(List<String> sentToPlayers) { this.sentToPlayers = sentToPlayers; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

}
