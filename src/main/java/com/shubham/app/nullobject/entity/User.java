package com.shubham.app.nullobject.entity;

public class User implements UserInterface {

    private Integer userId;
    private String name;

    public User(Integer userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    @Override
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", name='" + name + '\'' + '}';
    }
}
