package com.shubham.app.tictactoe.dao;

import com.shubham.app.tictactoe.entity.Board;
import com.shubham.app.tictactoe.entity.Cell;
import com.shubham.app.tictactoe.entity.Player;
import com.shubham.app.tictactoe.entity.Symbol;

import java.util.ArrayList;
import java.util.List;

public class BoardDAOImpl implements BoardDAO {

    private Board board;

    public BoardDAOImpl(Board board) {
        this.board = board;
    }

    public BoardDAOImpl(int rows) {
        createBoard(rows);
    }

    @Override
    public void fill(Player player, int x, int y) {
        updateCell(x, y, player.getSymbol());
    }

    @Override
    public Cell getCell(int x, int y) {
        if (x < 0 || x >= board.getRows() || y < 0 || y >= board.getRows()) {
            throw new RuntimeException("invalid coordinate (" + x + "," + y + ")");
        }

        int index = (x * board.getRows()) + y;
        if (index < 0 || index >= board.getCells().size()) {
            throw new RuntimeException("invalid coordinate (" + x + "," + y + ")");
        }

        return board.getCells().get(index);
    }

    @Override
    public List<Cell> getCells() {

        return board.getCells();
    }

    public void updateCell(int x, int y, Symbol symbol) {
        Cell cell = getCell(x, y);

        if (cell == null) {
            throw new RuntimeException("Unable to complete, request please try again later");
        }
        if (cell.getSymbol() != null) {
            throw new RuntimeException("Already taken by " + cell.getSymbol() + ", please select any other position");
        }

        cell.setSymbol(symbol);
    }

    public void createBoard(int rows) {
        if (rows <= 0 || rows > 5) {
            throw new RuntimeException("Invalid input parameter");
        }

        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                Cell cell = new Cell(i, j);
                cells.add(cell);
            }
        }

        this.board = new Board(rows, cells);
    }

    @Override
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
