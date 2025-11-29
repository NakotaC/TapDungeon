package com.example.tapdungeon;

import java.util.Map;

public class PlayerModel {
    private String userId;
    private String displayName;
    private String clan;
    private int health;
    private int gold;
    private int level;
    private int killsOnLevel;
    private Map<String, Object> upgrades;
    private Map<String, Object> skills;
    private int damagePerTap;
    private int damagePerSecond;
    private Map<String, Object> friends;


    PlayerModel() {
    }
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
        calculateHealth();
        calculateDamagePerTap(upgrades, skills);
        calculateDamagePerSecond(upgrades, skills);
    }

    public void calculateDamagePerTap(Map<String, Object> upgrades, Map<String, Object> skills){
        this.damagePerTap = this.level * 2;
    }

    public void calculateDamagePerSecond(Map<String, Object> upgrades, Map<String, Object> skills){
        this.damagePerSecond = this.level * 0;

    }
    public void calculateHealth(){
        this.health = this.level * 10;

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
    public int getLevel() {
        return level;
    }
    public int getGold() {
        return gold;
    }
    public int getDamagePerTap() {
        return damagePerTap;
    }
    public int getDamagePerSecond() {
        return damagePerSecond;
    }
    public int getKillsOnLevel() {
        return killsOnLevel;
    }


    public void enemyKilled(MonsterModel enemy){
        this.gold += enemy.getGold();
        killsOnLevel += 1;
        if (killsOnLevel % 10 == 0){
            this.level += 1;
            killsOnLevel = 0;
            calculateHealth();
            calculateDamagePerTap(upgrades, skills);
            calculateDamagePerSecond(upgrades, skills);
        }
    }
}

