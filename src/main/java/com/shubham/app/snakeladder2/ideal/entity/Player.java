package com.shubham.app.snakeladder2.ideal.entity;

public class Player {

    private final int id;
    private final String name;
    private int position;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.position = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void moveTo(int newPosition) {
        this.position = newPosition;
    }

    @Override
    public String toString() {
        return "Player{" + "id=" + id + ", name='" + name + '\'' + ", position=" + position + '}';
    }
}
