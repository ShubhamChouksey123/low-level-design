package com.shubham.app.tictactoe.entity;

public class Cell {

    private Integer x;
    private Integer y;
    private Symbol symbol;

    public Cell(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "Cell{" + "x=" + x + ", y=" + y + ", symbol=" + symbol + '}';
    }
}
