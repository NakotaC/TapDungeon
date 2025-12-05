package com.example.tapdungeon.data.model;

import java.util.List;

public interface InboxItemInterface {

    InboxItemType getItemType();
    String getId();
    long getTimestamp();

    List<String> getSentToPlayers();

}
