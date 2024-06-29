package com.shubham.app.tictactoe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.tictactoe.dao.BoardDAO;
import com.shubham.app.tictactoe.entity.Cell;
import com.shubham.app.tictactoe.entity.Player;
import com.shubham.app.tictactoe.entity.Symbol;

import java.util.List;
import java.util.Objects;

public class GamePlayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private BoardDAO boardDAO;
    private Player player1;
    private Player player2;

    public GamePlayService(BoardDAO boardDAO, Player player1, Player player2) {
        this.boardDAO = boardDAO;
        this.player1 = player1;
        this.player2 = player2;
    }

    public void move(Player player, int x, int y) {
        boardDAO.fill(player, x, y);
        decideWinner(player);
    }

    private boolean checkRow(int row, List<Cell> cells, Symbol symbol) {
        int startIndex = boardDAO.getBoard().getRows() * row;
        int count = 0;
        for (int i = startIndex; i < row + startIndex; i++) {
            if (cells.get(i) != null && Objects.equals(cells.get(i).getSymbol(), symbol)) {
                count++;
            }
        }
        return (count == boardDAO.getBoard().getRows());
    }

    private boolean checkColumn(int column, List<Cell> cells, Symbol symbol) {
        int count = 0;
        for (int i = column; i < boardDAO.getBoard().getRows() * boardDAO.getBoard().getRows(); i = i
                + boardDAO.getBoard().getRows()) {
            if (cells.get(i) != null && Objects.equals(cells.get(i).getSymbol(), symbol)) {
                count++;
            }
        }

        return (count == boardDAO.getBoard().getRows());
    }

    private boolean decideWinner(Player player) {
        List<Cell> cells = boardDAO.getCells();
        Symbol symbol = player.getSymbol();

        for (int row = 0; row < boardDAO.getBoard().getRows(); row++) {
            if (checkRow(row, cells, symbol)) {
                logger.info("player : {}, is the winner, the row : {}, is full", player, row);
                return true;
            }
        }

        for (int column = 0; column < boardDAO.getBoard().getRows(); column++) {
            if (checkColumn(column, cells, symbol)) {
                logger.info("player : {}, is the winner, the column : {} is full", player, column);
                return true;
            }
        }

        return false;
    }

    public void show() {
        List<Cell> cells = boardDAO.getCells();
        int rows = boardDAO.getBoard().getRows();

        for (int i = 0; i < rows; i++) {

            String str = "";
            for (int j = 0; j < rows; j++) {
                if (boardDAO.getCell(i, j).getSymbol() != null) {
                    str += " " + boardDAO.getCell(i, j).getSymbol().toString();
                } else {
                    str += " " + ".";
                }
            }
            logger.info("str : " + str);
        }
    }
}
