package com.shubham.app.votingalgorithm.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.votingalgorithm.ideal.exception.InvalidBallotException;
import com.shubham.app.votingalgorithm.ideal.exception.InvalidVotingConfigException;
import com.shubham.app.votingalgorithm.ideal.service.EarliestToReachTieBreakStrategy;
import com.shubham.app.votingalgorithm.ideal.service.RankPositionTieBreakStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VotingServiceTest {

    @Test
    void constructorRejectsANullTieBreakStrategy() {
        assertThrows(InvalidVotingConfigException.class, () -> new VotingService(null));
    }

    @Test
    void getResultsRejectsANullBallotList() {
        VotingService votingService = new VotingService(new RankPositionTieBreakStrategy());
        assertThrows(InvalidBallotException.class, () -> votingService.getResults(null));
    }

    // The exact base-case example from the problem's source, verified by hand:
    // A=3+2+2=7,
    // B=2+3+1=6, C=1+1+3=5.
    @Test
    void sortsCandidatesByTotalPointsDescendingWhenThereIsNoTie() {
        VotingService votingService = new VotingService(new RankPositionTieBreakStrategy());
        List<Ballot> ballots = List.of(new Ballot(List.of("A", "B", "C")), new Ballot(List.of("B", "A", "C")),
                new Ballot(List.of("C", "A", "B")));

        assertEquals(List.of("A", "B", "C"), votingService.getResults(ballots));
    }

    // The practice code's fixed `for (i = 0; i < 3; i++)` loop crashed on any
    // ballot shorter
    // than 3 - a voter ranking fewer than 3 candidates is explicitly valid per the
    // problem
    // statement ("up to three").
    @Test
    void handlesBallotsThatRankFewerThanThreeCandidates() {
        VotingService votingService = new VotingService(new EarliestToReachTieBreakStrategy());
        List<Ballot> ballots = List.of(new Ballot(List.of("A")), new Ballot(List.of("B")));

        assertEquals(List.of("A", "B"), votingService.getResults(ballots));
    }

    // Proves the two strategies genuinely disagree on the same input: A and B both
    // tie at 3
    // points, but RankPosition favors A (more 1st-place votes) while
    // EarliestToReach favors B
    // (B's score was locked in at ballot index 1, A's only at index 2).
    @Test
    void theTwoTieBreakStrategiesCanDisagreeOnTheSameInput() {
        List<Ballot> ballots = List.of(new Ballot(List.of("X", "B", "Y")), new Ballot(List.of("X", "Y", "B")),
                new Ballot(List.of("A", "X", "Y")));

        VotingService rankPositionService = new VotingService(new RankPositionTieBreakStrategy());
        VotingService earliestToReachService = new VotingService(new EarliestToReachTieBreakStrategy());

        assertEquals(List.of("X", "Y", "A", "B"), rankPositionService.getResults(ballots));
        assertEquals(List.of("X", "Y", "B", "A"), earliestToReachService.getResults(ballots));
    }

    // Regression test for the practice code's static-field bug: constructing a
    // second
    // VotingService with a different strategy must not change the behavior of a
    // previously-constructed, still-referenced instance.
    @Test
    void twoVotingServiceInstancesWithDifferentStrategiesDoNotInterfereWithEachOther() {
        List<Ballot> ballots = List.of(new Ballot(List.of("X", "B", "Y")), new Ballot(List.of("X", "Y", "B")),
                new Ballot(List.of("A", "X", "Y")));

        VotingService rankPositionService = new VotingService(new RankPositionTieBreakStrategy());
        List<String> firstResult = rankPositionService.getResults(ballots);

        new VotingService(new EarliestToReachTieBreakStrategy());

        assertEquals(firstResult, rankPositionService.getResults(ballots));
    }
}
