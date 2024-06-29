package com.shubham.app.nullobject.entity;

public class UserNull implements UserInterface {
    @Override
    public Integer getUserId() {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }
}
