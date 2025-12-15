package com.example.tapdungeon.social.clan;

/**
 * Model class for the clan object.
 */
public class Clan {
    public String id;
    private String name;

    public Clan() {}

    /**
     * Constructor for the clan object.
     * @param id id of the clan
     * @param name name of the clan
     */
    public Clan(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Getter for the id of the clan.
     * @return id of the clan
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the name of the clan.
     * @return name of the clan
     */
    public String getName() {
        return name;
    }
}
