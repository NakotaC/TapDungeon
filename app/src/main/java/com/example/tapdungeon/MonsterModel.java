package com.example.tapdungeon;

import java.util.Random;

/**
 * Model class for the monster object.
 */
public class MonsterModel {

    private String type;
    private enum enemyType {
        skeleton,
        slime,
        zombie,
        orc,
    }
    private int health;
    private int damage;
    private int gold;
    private final Random rand = new Random();
    private boolean isDead;

    /**
     * Constructor for the monster object.
     * @param level level of the monster
     */
    MonsterModel(int level){
        rand.nextInt();
        this.type = enemyType.values()[rand.nextInt(enemyType.values().length)].toString();
        this.health = level * 10;
        this.gold = level * 5;
        this.damage = level * 2;
        isDead = false;
    }

    /**
     * Getter for the monster's type.
     * @return monster's type
     */
    public String getType(){
        return this.type;
    }

    /**
     * Getter for the monster's health.
     * @return monster's health
     */
    public int getHealth(){
        return this.health;
    }

    /**
     * Getter for the monster's damage.
     * @return monster's damage
     */
    public int getDamage(){
        return this.damage;
    }

    /**
     * Getter for the gold dropped by the monster.
     * @return gold dropped
     */
    public int getGold(){
        return this.gold;
    }

    /**
     * Method for when the monster takes damage.
     * @param damage amount of damage taken
     */
    public void takeDamage(int damage){
        this.health = Math.max(0, this.health - damage);
        if (this.health == 0){
            isDead = true;
        }
    }

    /**
     * Getter for the monster's death status.
     * @return monster's death status
     */
    public boolean isDead(){
        return this.isDead;
    }

}
