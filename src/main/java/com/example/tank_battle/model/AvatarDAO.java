package com.example.tank_battle.model;

public class AvatarDAO {

    private String name;
    private int wins;

    public AvatarDAO(String name) {
        this.name = name;
        wins = 0;
    }

    public AvatarDAO(String name, int wins) {
        this.name = name;
        this.wins = wins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
