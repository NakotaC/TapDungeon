package com.example.tapdungeon;

import java.util.Random;

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

    MonsterModel(int level){
        rand.nextInt();
        this.type = enemyType.values()[rand.nextInt(enemyType.values().length)].toString();
        this.health = level * 10;
        this.gold = level * 5;
        this.damage = level * 2;
        isDead = false;
    }

    public String getType(){
        return this.type;
    }

    public int getHealth(){
        return this.health;
    }

    public int getDamage(){
        return this.damage;
    }

    public int getGold(){
        return this.gold;
    }

    public void takeDamage(int damage){
        this.health = Math.max(0, this.health - damage);
        if (this.health == 0){
            isDead = true;
        }
    }
public boolean isDead(){
        return this.isDead;
}

}
