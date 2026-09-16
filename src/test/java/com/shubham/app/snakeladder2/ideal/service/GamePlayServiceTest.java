package com.shubham.app.snakeladder2.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakeladder2.ideal.entity.Board;
import com.shubham.app.snakeladder2.ideal.entity.BoardJump;
import com.shubham.app.snakeladder2.ideal.entity.Dice;
import com.shubham.app.snakeladder2.ideal.entity.Ladder;
import com.shubham.app.snakeladder2.ideal.entity.Player;
import com.shubham.app.snakeladder2.ideal.entity.Snake;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePlayServiceTest {

    @Test
    void rollAdvancesPlayerByTheRolledAmount() {
        Board board = new Board(100, List.<BoardJump>of());
        Player player = new Player(0, "Shubham");
        GamePlayService service = new GamePlayService(board, List.of(player), new FixedDice(4));

        boolean gameOver = service.playTurn(player);

        assertFalse(gameOver);
        assertEquals(4, player.getPosition());
    }

    @Test
    void landingOnASnakeMovesPlayerToTheSnakesTail() {
        Board board = new Board(100, List.<BoardJump>of(new Snake(5, 2)));
        Player player = new Player(0, "Shubham");
        GamePlayService service = new GamePlayService(board, List.of(player), new FixedDice(5));

        service.playTurn(player);

        assertEquals(2, player.getPosition());
    }

    @Test
    void landingOnALadderMovesPlayerToTheLaddersTop() {
        Board board = new Board(100, List.<BoardJump>of(new Ladder(5, 67)));
        Player player = new Player(0, "Shubham");
        GamePlayService service = new GamePlayService(board, List.of(player), new FixedDice(5));

        service.playTurn(player);

        assertEquals(67, player.getPosition());
    }

    @Test
    void rollThatOvershootsTheBoardDoesNotMoveThePlayer() {
        Board board = new Board(10, List.<BoardJump>of());
        Player player = new Player(0, "Shubham");
        GamePlayService service = new GamePlayService(board, List.of(player), new FixedDice(5));
        player.moveTo(8);

        boolean gameOver = service.playTurn(player);

        assertFalse(gameOver);
        assertEquals(8, player.getPosition());
    }

    @Test
    void reachingTheExactLastSquareWinsTheGame() {
        Board board = new Board(10, List.<BoardJump>of());
        Player player = new Player(0, "Shubham");
        GamePlayService service = new GamePlayService(board, List.of(player), new FixedDice(3));
        player.moveTo(7);

        boolean gameOver = service.playTurn(player);

        assertTrue(gameOver);
        assertTrue(service.isGameOver());
        assertEquals(player, service.getWinner().orElseThrow());
    }

    @Test
    void secondPlayersTurnInTheSameRoundDoesNotCrashOnceFirstPlayerWins() {
        Board board = new Board(5, List.<BoardJump>of());
        Player p1 = new Player(0, "P1");
        Player p2 = new Player(1, "P2");
        Dice dice = new FixedDice(5, 3);
        GamePlayService service = new GamePlayService(board, List.of(p1, p2), dice);

        boolean p1Ended = service.playTurn(p1);
        assertTrue(p1Ended);
        assertEquals(p1, service.getWinner().orElseThrow());

        boolean p2Ended = assertDoesNotThrow(() -> service.playTurn(p2));

        assertTrue(p2Ended);
        assertEquals(0, p2.getPosition());
        assertEquals(p1, service.getWinner().orElseThrow());
    }
}
