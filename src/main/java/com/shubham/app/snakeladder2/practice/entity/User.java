package com.shubham.app.snakeladder2.practice.entity;

public class User {

    private int userId;
    private String name;
    private int position;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.position = 0;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", name='" + name + '\'' + ", position=" + position + '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
