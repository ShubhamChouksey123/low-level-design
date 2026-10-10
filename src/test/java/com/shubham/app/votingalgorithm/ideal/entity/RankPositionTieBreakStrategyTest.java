package com.shubham.app.votingalgorithm.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.votingalgorithm.ideal.service.RankPositionTieBreakStrategy;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Lives in entity/, not service/, because it needs package-private access to
// CandidateTally.recordVote to build test fixtures directly - the same reason
// CandidateTally keeps that method package-private rather than public.
class RankPositionTieBreakStrategyTest {

    private final RankPositionTieBreakStrategy strategy = new RankPositionTieBreakStrategy();

    @Test
    void moreFirstPlaceVotesRanksFirst() {
        CandidateTally a = new CandidateTally("A");
        a.recordVote(0, 0);
        CandidateTally b = new CandidateTally("B");
        b.recordVote(1, 0);
        b.recordVote(2, 1);

        assertTrue(strategy.compare(a, b) < 0);
    }

    @Test
    void fallsBackToAlphabeticalWhenEveryPositionCountTies() {
        CandidateTally a = new CandidateTally("A");
        a.recordVote(0, 0);
        CandidateTally z = new CandidateTally("Z");
        z.recordVote(0, 1);

        assertTrue(strategy.compare(a, z) < 0);
    }
}
