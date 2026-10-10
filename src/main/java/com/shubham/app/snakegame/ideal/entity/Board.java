package com.shubham.app.snakegame.ideal.entity;

import com.shubham.app.snakegame.ideal.exception.InvalidBoardConfigException;

/**
 * Just the grid's dimensions - no {@code foods} field. Food is specific to one
 * growth variant (see {@link GrowthPolicy}); the reported "every 5 moves"
 * variant has no food at all, so it doesn't belong on the one entity both
 * variants share. Immutable - the practice code's {@code
 * setHeight}/{@code setWidth} could desync a growth/collision check from the
 * snake's own occupancy tracking if ever called after construction.
 */
public class Board {

    private final int height;
    private final int width;

    public Board(int height, int width) {
        if (height <= 0) {
            throw new InvalidBoardConfigException("height must be positive: " + height);
        }
        if (width <= 0) {
            throw new InvalidBoardConfigException("width must be positive: " + width);
        }
        this.height = height;
        this.width = width;
    }

    public boolean isOutside(Position position) {
        return position.x() < 0 || position.x() >= height || position.y() < 0 || position.y() >= width;
    }
}
