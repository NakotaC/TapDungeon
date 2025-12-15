package com.example.tapdungeon.data.model;

import java.util.List;

/**
 * Interface for random events.
 */
public class RandomEvent implements InboxItemInterface {

    private String id;
    private String title;
    private String description;
    private long timestamp;
    private List<String> sentToPlayers;
    private String type; // stored as RANDOM_EVENT

    public RandomEvent() { }

    /**
     * Constructor for the random event.
     * @param id id of the random event
     * @param title title of the random event
     * @param description description of the random event
     * @param timestamp timestamp of the random event
     * @param sentToPlayers list of players the random event is sent to
     */
    public RandomEvent(String id, String title, String description, long timestamp, List<String> sentToPlayers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.sentToPlayers = sentToPlayers;
        this.type = InboxItemType.RANDOM_EVENT.name();
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
