package com.shubham.app.votingalgorithm.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.votingalgorithm.ideal.service.EarliestToReachTieBreakStrategy;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EarliestToReachTieBreakStrategyTest {

    private final EarliestToReachTieBreakStrategy strategy = new EarliestToReachTieBreakStrategy();

    @Test
    void theCandidateWhoseLastContributingBallotIsEarlierRanksFirst() {
        CandidateTally a = new CandidateTally("A");
        a.recordVote(0, 0);
        CandidateTally b = new CandidateTally("B");
        b.recordVote(0, 1);

        assertTrue(strategy.compare(a, b) < 0);
    }

    @Test
    void fallsBackToAlphabeticalWhenTheLastContributingBallotIsTheSameIndex() {
        CandidateTally a = new CandidateTally("A");
        a.recordVote(0, 0);
        CandidateTally z = new CandidateTally("Z");
        z.recordVote(1, 0);

        assertTrue(strategy.compare(a, z) < 0);
    }
}
