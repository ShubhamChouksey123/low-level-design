package com.shubham.app.snakegame.practice.entity;

import java.util.List;

public class Board {

    private int height;
    private int width;
    private List<Position> foods;

    public Board(int height, int width, List<Position> foods) {
        this.height = height;
        this.width = width;
        this.foods = foods;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<Position> getFoods() {
        return foods;
    }

    public void setFoods(List<Position> foods) {
        this.foods = foods;
    }
}
