package com.shubham.app.snakeladder.game.play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.snakeladder.game.create.Ladder;
import com.shubham.app.snakeladder.game.create.Snake;
import com.shubham.app.snakeladder.service.GameCreateService;
import com.shubham.app.snakeladder.user.Player;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PlayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private Random random;

    public PlayService(Random random) {
        this.random = random;
    }

    private int rollDice() {
        return 1 + random.nextInt(6);
    }

    protected void movePlayer(Player player, GameCreateService gameCreateService) {

        int diceValue = rollDice();
        int nextPosition = player.getLocation() + diceValue;

        logger.info("{} rolled a {} and moved from {} to {}", player.getName(), diceValue, player.getLocation(),
                nextPosition);
        nextPosition = getNextPosition(nextPosition, gameCreateService);

        if (nextPosition == 100) {
            logger.info("{} is the winner", player.getName());
        }
        if (nextPosition <= 100) {
            player.setLocation(nextPosition);
        }
    }

    private Integer getNextPosition(Integer currentPosition, GameCreateService gameCreateService) {

        while (!isLocationFinal(currentPosition, gameCreateService)) {
            currentPosition = getNextPositionAfterSnakeBite(currentPosition, gameCreateService);
            currentPosition = getNextPositionAfterLadderClimb(currentPosition, gameCreateService);
        }

        return currentPosition;
    }

    private boolean isLocationFinal(Integer currentPosition, GameCreateService gameCreateService) {
        List<Snake> snakes = gameCreateService.getBoard().getSnakes();

        for (Snake snake : snakes) {
            if (Objects.equals(snake.getHead(), currentPosition)) {
                return false;
            }
        }

        List<Ladder> ladders = gameCreateService.getBoard().getLadders();

        for (Ladder ladder : ladders) {
            if (Objects.equals(ladder.getStart(), currentPosition)) {
                return false;
            }
        }

        return true;
    }

    private Integer getNextPositionAfterSnakeBite(Integer currentPosition, GameCreateService gameCreateService) {
        List<Snake> snakes = gameCreateService.getBoard().getSnakes();

        for (Snake snake : snakes) {
            if (Objects.equals(snake.getHead(), currentPosition)) {
                logger.info("Bad luck a snake bite and moved from {} to {}", currentPosition, snake.getTail());
                return snake.getTail();
            }
        }

        return currentPosition;
    }

    private Integer getNextPositionAfterLadderClimb(Integer currentPosition, GameCreateService gameCreateService) {
        List<Ladder> ladders = gameCreateService.getBoard().getLadders();

        for (Ladder ladder : ladders) {
            if (Objects.equals(ladder.getStart(), currentPosition)) {
                logger.info("Hurray climbed a ladder and moved from {} to {}", currentPosition, ladder.getEnd());
                return ladder.getEnd();
            }
        }

        return currentPosition;
    }

    private boolean isSnakeExists(Integer currentPosition, GameCreateService gameCreateService) {
        List<Snake> snakes = gameCreateService.getBoard().getSnakes();

        for (Snake snake : snakes) {
            if (Objects.equals(snake.getHead(), currentPosition)) {
                return true;
            }
        }

        return false;
    }

    private boolean isLadderExists(Integer currentPosition, GameCreateService gameCreateService) {
        List<Ladder> ladders = gameCreateService.getBoard().getLadders();

        for (Ladder ladder : ladders) {
            if (Objects.equals(ladder.getStart(), currentPosition)) {
                return true;
            }
        }

        return false;
    }
}
