package com.shubham.app.tictactoe.entity;

import java.util.List;

public class Board {

    private Integer rows;
    private List<Cell> cells;

    public Board(Integer rows, List<Cell> cells) {
        this.rows = rows;
        this.cells = cells;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "Board{" + "cells=" + cells + '}';
    }
}
