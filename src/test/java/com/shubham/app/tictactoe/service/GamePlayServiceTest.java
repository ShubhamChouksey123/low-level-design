package com.shubham.app.tictactoe.service;

import org.junit.jupiter.api.Test;
import org.slf4j.*;

import com.shubham.app.tictactoe.dao.BoardDAO;
import com.shubham.app.tictactoe.dao.BoardDAOImpl;
import com.shubham.app.tictactoe.entity.Player;
import com.shubham.app.tictactoe.entity.Symbol;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GamePlayServiceTest {

    Player player1 = new Player("Ram", Symbol.O);
    Player player2 = new Player("Shyam", Symbol.X);

    private final Logger logger = LoggerFactory.getLogger(GamePlayServiceTest.class);

    @Test
    void testGamePlayService() {

        BoardDAO boardDAO = new BoardDAOImpl(3);
        GamePlayService gamePlayService = new GamePlayService(boardDAO, player1, player2);

        assertNotNull(gamePlayService);
    }

    @Test
    void testGamePlayService2() {

        logger.info("starting new test");

        BoardDAO boardDAO = new BoardDAOImpl(3);
        GamePlayService gamePlayService = new GamePlayService(boardDAO, player1, player2);

        assertNotNull(gamePlayService);

        gamePlayService.move(player1, 1, 1);
        gamePlayService.move(player2, 0, 2);

        gamePlayService.move(player1, 1, 0);
        gamePlayService.move(player2, 1, 2);

        gamePlayService.move(player1, 2, 2);
        gamePlayService.move(player2, 0, 0);

        gamePlayService.move(player1, 2, 1);
        gamePlayService.move(player2, 2, 0);

        gamePlayService.move(player1, 0, 1);

        gamePlayService.show();
    }

    @Test
    void testGamePlayService3() {

        logger.info("starting new test");
        BoardDAO boardDAO = new BoardDAOImpl(3);
        GamePlayService gamePlayService = new GamePlayService(boardDAO, player1, player2);

        assertNotNull(gamePlayService);

        gamePlayService.move(player1, 1, 0);
        gamePlayService.move(player2, 0, 0);

        gamePlayService.move(player1, 0, 2);
        gamePlayService.move(player2, 2, 0);

        gamePlayService.move(player1, 1, 2);
        gamePlayService.move(player2, 1, 1);

        gamePlayService.move(player1, 2, 2);

        gamePlayService.show();
    }
}
