package com.shubham.app.snakegame.ideal.entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * The snake's body (head at the front, tail at the back) plus a {@link Set} of
 * occupied cells for O(1) collision checks. Deliberately a
 * {@code Set<Position>}, not a {@code boolean[][]} sized from the board's
 * dimensions - the practice code's grid meant {@code Snake} depended on
 * {@code Board}'s dimensions never changing after construction; a set has no
 * such dependency at all.
 */
public class Snake {

    private final Deque<Position> body = new ArrayDeque<>();
    private final Set<Position> occupied = new HashSet<>();

    public Snake(Position start) {
        body.offerFirst(start);
        occupied.add(start);
    }

    public Position getHead() {
        return body.peekFirst();
    }

    public int length() {
        return body.size();
    }

    /**
     * Whether moving to {@code nextPosition} would be a self-collision, given
     * whether this move grows the snake. The practice code's equivalent check
     * ({@code Snake.isOccupied}) didn't take {@code
     * grow} into account at all: when the snake isn't growing, its current tail
     * cell is vacated in the very same step the head arrives - so a head moving
     * exactly onto the current tail (e.g. an immediate reversal, or looping back
     * onto its own trailing cell) is not actually a collision. Verified against the
     * practice code's bug: a length-2 snake reversing onto its own tail was wrongly
     * treated as game-over.
     */
    public boolean wouldCollide(Position nextPosition, boolean grow) {
        if (!occupied.contains(nextPosition)) {
            return false;
        }
        if (!grow && nextPosition.equals(body.peekLast())) {
            return false;
        }
        return true;
    }

    void move(Position nextHead, boolean grow) {
        body.offerFirst(nextHead);
        occupied.add(nextHead);
        if (!grow) {
            Position tail = body.pollLast();
            occupied.remove(tail);
        }
    }
}
