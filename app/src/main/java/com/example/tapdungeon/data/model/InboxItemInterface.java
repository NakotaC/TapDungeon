package com.example.tapdungeon.data.model;

import java.util.List;

/**
 * Interface for the inbox item types.
 */
public interface InboxItemInterface {

    InboxItemType getItemType();
    String getId();
    long getTimestamp();

    List<String> getSentToPlayers();

}
