package com.example.tapdungeon.data.model;

import java.util.List;

/**
 * Interface for scheduled events.
 */
public class ScheduledEvent implements InboxItemInterface {

    private String id;
    private String title;
    private String description;
    private long timestamp;
    private List<String> sentToPlayers;
    private String type; // stored as SCHEDULED_EVENT

    /**
     * Constructor for the scheduled event.
     * @param id id of the scheduled event
     * @param title title of the scheduled event
     * @param description description of the scheduled event
     * @param timestamp timestamp of the scheduled event
     * @param sentToPlayers list of players the scheduled event is sent to
     */
    public ScheduledEvent(String id, String title, String description, long timestamp, List<String> sentToPlayers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.sentToPlayers = sentToPlayers;
        this.type = InboxItemType.SCHEDULED_EVENT.name();
    }

    /**
     * Returns the type of the item.
     * @return the type of the item
     */
    @Override
    public InboxItemType getItemType() {
        return InboxItemType.valueOf(type);
    }

    /**
     * Returns the timestamp of the item.
     * @return the timestamp of the item
     */
    @Override
    public long getTimestamp() { return timestamp; }

    /**
     * Returns the id of the item.
     * @return the id of the item
     */
    @Override
    public String getId() { return id; }

    /**
     * Returns the list of players the item is sent to.
     * @return the list of players the item is sent to
     */
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
