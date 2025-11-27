package com.example.tapdungeon.data.model;

import java.util.Map;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String clan;
    private Long level;
    private Long gold;
    private Map<String, Object> friends;



    public LoggedInUser(String userId, String displayName, String clan, Long level, Long gold, Map<String, Object> friends) {
        this.userId = userId;
        this.displayName = displayName;
        this.clan = clan;
        this.level = level;
        this.gold = gold;
        this.friends = friends;
    }

    public Map<String, Object> getFriends() {
        return friends;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getClan() {
        return clan;
    }
    public Long getLevel() {
        return level;
    }
    public Long getGold() {
        return gold;
    }
}