package com.shubham.app.tictactoe.dao;

import com.shubham.app.tictactoe.entity.Board;
import com.shubham.app.tictactoe.entity.Cell;
import com.shubham.app.tictactoe.entity.Player;

import java.util.List;

public interface BoardDAO {

    void fill(Player player, int x, int y);

    Cell getCell(int x, int y);

    List<Cell> getCells();

    Board getBoard();
}
