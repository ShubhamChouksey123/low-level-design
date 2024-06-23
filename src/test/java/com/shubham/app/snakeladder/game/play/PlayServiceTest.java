package com.shubham.app.snakeladder.game.play;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.snakeladder.game.create.Board;
import com.shubham.app.snakeladder.game.create.Ladder;
import com.shubham.app.snakeladder.game.create.Snake;
import com.shubham.app.snakeladder.service.GameCreateService;
import com.shubham.app.snakeladder.user.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeAll
    static void beforeAllInit() {
        System.out.println("Before All - Before the class has been created !");
    }

    @BeforeEach
    void init() {
        System.out.println("beforeEach Method Run !");
    }

    @AfterEach
    void cleanUp() {
        System.out.println("after each method has fininshed running. Cleaning up !");
    }

    private GameCreateService createGameAndPlayers() {
        /** 62 5 33 6 49 9 88 16 41 20 56 53 98 64 93 73 95 75 */
        List<Snake> snakes = new ArrayList<>() {
            {
                add(new Snake(62, 5));
                add(new Snake(33, 6));
                add(new Snake(49, 9));
                add(new Snake(88, 16));
                add(new Snake(56, 53));
                add(new Snake(98, 64));
                add(new Snake(93, 73));
                add(new Snake(95, 75));
            }
        };

        /** 2 37 27 46 10 32 51 68 61 79 65 84 71 91 81 100 */
        List<Ladder> ladders = new ArrayList<>() {
            {
                add(new Ladder(2, 37));
                add(new Ladder(27, 46));
                add(new Ladder(10, 32));
                add(new Ladder(51, 68));
                add(new Ladder(61, 79));
                add(new Ladder(65, 84));
                add(new Ladder(71, 91));
                add(new Ladder(81, 100));
            }
        };

        Board board = new Board(snakes, ladders);

        List<Player> players = new ArrayList<>() {
            {
                add(new Player("Nikhil"));
                add(new Player("Duggu"));
            }
        };

        return new GameCreateService(board, players);
    }

    @Test
    void testRangeOfLocation() {

        PlayService playService = new PlayService(new Random());

        GameCreateService gameCreateService = createGameAndPlayers();
        logger.info("gameCreateService : {}", gameCreateService);

        playService.movePlayer(gameCreateService.getPlayers().get(0), gameCreateService);

        logger.info("Player positions : {}", gameCreateService.getPlayers());
        assertNotNull(gameCreateService.getPlayers().get(0).getLocation());
        // ass(gameCreateService.getPlayers().get(0).getLocation());
        assertTrue(gameCreateService.getPlayers().get(0).getLocation() > 0);
        assertTrue(gameCreateService.getPlayers().get(0).getLocation() < 7
                || gameCreateService.getPlayers().get(0).getLocation() == 37);
    }

    @Test
    void testConvertStringQuestionsToList() {

        PlayService playService = new PlayService(new Random());

        GameCreateService gameCreateService = createGameAndPlayers();
        logger.info("gameCreateService : {}", gameCreateService);

        int times = 100;
        while (times-- > 0) {
            for (Player player : gameCreateService.getPlayers()) {
                if (player.getLocation() != 100) {
                    playService.movePlayer(player, gameCreateService);
                }
            }
        }

        // playService.movePlayer(gameCreateService.getPlayers().get(0),
        // gameCreateService);

        logger.info("Player positions : {}", gameCreateService.getPlayers());
    }
}
