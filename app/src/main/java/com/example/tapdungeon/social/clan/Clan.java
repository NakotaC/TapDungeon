package com.example.tapdungeon.social.clan;

public class Clan {
    public String id;
    private String name;

    public Clan() {}

    public Clan(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
