package com.shubham.app.snakeladder.user;

public class Player {

    private String name;
    private Integer location;

    public Player(String name, Integer location) {
        this.name = name;
        this.location = location;
    }

    public Player(String name) {
        this.name = name;
        this.location = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + name + '\'' + ", location=" + location + '}';
    }
}
