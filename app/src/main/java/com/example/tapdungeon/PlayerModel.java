package com.example.tapdungeon;


import java.util.Map;

/**
 * Model class for the player object.
 */
public class PlayerModel {
    private String userId;
    private String displayName;
    private String clan;
    private int gold;
    private int level;
    private int killsOnLevel;
    private Map<String, Object> upgrades;
    private Map<String, Object> skills;
    private int damagePerTap;
    private int damagePerSecond;
    private Map<String, Object> friends;

    /**
     * Constructor for the player object.
     * @param userId user's unique ID
     * @param displayName user's display name
     * @param clan user's clan
     * @param level user's level
     * @param gold user's gold
     * @param friends user's friends
     * @param upgrades user's upgrades
     * @param skills user's skills
     * @param killsOnLevel user's kills on current level
     */
    PlayerModel(String userId, String displayName, String clan, Long level, Long gold, Map<String, Object> friends,Map<String, Object> upgrades, Map<String, Object> skills, Long killsOnLevel){
        this.userId = userId;
        this.displayName = displayName;
        this.clan = clan;
        this.gold = gold.intValue();
        this.level = level.intValue();
        this.upgrades = upgrades;
        this.killsOnLevel = killsOnLevel.intValue();
        this.skills = skills;
        this.friends = friends;
        calculateDamagePerTap(upgrades, skills);
        calculateDamagePerSecond(upgrades, skills);
    }

    /**
     * Calculates the damage per tap based on the level and upgrades.
     * @param upgrades users upgrades
     * @param skills users skills
     */
    public void calculateDamagePerTap(Map<String, Object> upgrades, Map<String, Object> skills){
        this.damagePerTap = this.level * 2;
    }

    /**
     * Calculates the damage per second based on the level and upgrades.
     * @param upgrades users upgrades
     * @param skills users skills
     */
    public void calculateDamagePerSecond(Map<String, Object> upgrades, Map<String, Object> skills){
        this.damagePerSecond = this.level * 0;

    }

    /**
     * Getter for the friends map.
     * @return friends map
     */
    public Map<String, Object> getFriends() {
        return friends;
    }

    /**
     * Getter for the user's display name.
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Getter for the user's clan.
     * @return user's clan
     */
    public String getClan() {
        return clan;
    }

    /**
     * Getter for the user's level.
     * @return user's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter for the user's gold.
     * @return user's gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * Getter for the user's damage per tap.
     * @return user's damage per tap
     */
    public int getDamagePerTap() {
        return damagePerTap;
    }

    /**
     * Getter for the user's damage per second.
     * @return user's damage per second
     */
    public int getDamagePerSecond() {
        return damagePerSecond;
    }

    /**
     * Getter for the user's kills on current level.
     * @return user's kills on current level
     */
    public int getKillsOnLevel() {
        return killsOnLevel;
    }

    /**
     * Getter for the user's upgrades.
     * @return user's upgrades
     */
    public Map<String, Object> getUpgrades() {
        return upgrades;
    }

    /**
     * Getter for the user's skills.
     * @return user's skills
     */
    public Map<String, Object> getSkills() {
        return skills;
    }


    /**
     * Method for when an enemy is defeated. Handles moving on to the next level at 10 kills and recalulating damage.
     * @param enemy enemy that was defeated
     */
    public void enemyKilled(MonsterModel enemy){
        this.gold += enemy.getGold();
        killsOnLevel += 1;
        if (killsOnLevel % 10 == 0){
            this.level += 1;
            killsOnLevel = 0;
            calculateDamagePerTap(upgrades, skills);
            calculateDamagePerSecond(upgrades, skills);
        }
    }

    /**
     * Adds gold to the player's gold.
     * @param goldEarned amount of gold to add
     */
    public void addGold(long goldEarned) {
        this.gold += (int) goldEarned * this.level;
    }
}

