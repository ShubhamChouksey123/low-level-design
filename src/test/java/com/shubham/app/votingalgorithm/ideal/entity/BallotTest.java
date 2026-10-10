package com.shubham.app.votingalgorithm.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.votingalgorithm.ideal.exception.InvalidBallotException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BallotTest {

    @Test
    void constructorRejectsANullOrEmptyBallot() {
        assertThrows(InvalidBallotException.class, () -> new Ballot(null));
        assertThrows(InvalidBallotException.class, () -> new Ballot(List.of()));
    }

    @Test
    void constructorRejectsMoreThanThreeRankedCandidates() {
        assertThrows(InvalidBallotException.class, () -> new Ballot(List.of("A", "B", "C", "D")));
    }

    @Test
    void constructorAllowsOneOrTwoRankedCandidates() {
        new Ballot(List.of("A"));
        new Ballot(List.of("A", "B"));
        new Ballot(List.of("A", "B", "C"));
    }

    @Test
    void constructorRejectsABlankOrNullCandidateName() {
        List<String> withBlank = new ArrayList<>();
        withBlank.add("A");
        withBlank.add("  ");
        assertThrows(InvalidBallotException.class, () -> new Ballot(withBlank));

        List<String> withNull = new ArrayList<>();
        withNull.add("A");
        withNull.add(null);
        assertThrows(InvalidBallotException.class, () -> new Ballot(withNull));
    }

    @Test
    void constructorRejectsTheSameCandidateRankedTwice() {
        assertThrows(InvalidBallotException.class, () -> new Ballot(List.of("A", "A", "B")));
    }
}
