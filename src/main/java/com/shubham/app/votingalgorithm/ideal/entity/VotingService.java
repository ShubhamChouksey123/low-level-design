package com.shubham.app.votingalgorithm.ideal.entity;

import com.shubham.app.votingalgorithm.ideal.exception.InvalidBallotException;
import com.shubham.app.votingalgorithm.ideal.exception.InvalidVotingConfigException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tallies a list of ballots and returns every candidate named on any ballot,
 * sorted by total points descending, breaking ties with the configured
 * {@link TieBreakStrategy}.
 *
 * <p>
 * {@code tieBreakStrategy} is a plain instance field, set once in the
 * constructor - the practice code's equivalent field was {@code static}, so
 * constructing a second {@code ResultService} with a different strategy
 * silently changed the behavior of every previously-constructed instance too,
 * including ones never meant to be reconfigured.
 */
public class VotingService {

    private final TieBreakStrategy tieBreakStrategy;

    public VotingService(TieBreakStrategy tieBreakStrategy) {
        if (tieBreakStrategy == null) {
            throw new InvalidVotingConfigException("tieBreakStrategy must not be null");
        }
        this.tieBreakStrategy = tieBreakStrategy;
    }

    public List<String> getResults(List<Ballot> ballots) {
        if (ballots == null) {
            throw new InvalidBallotException("ballots must not be null");
        }

        Map<String, CandidateTally> talliesByName = new HashMap<>();
        for (int ballotIndex = 0; ballotIndex < ballots.size(); ballotIndex++) {
            List<String> rankedCandidates = ballots.get(ballotIndex).getRankedCandidates();
            for (int rank = 0; rank < rankedCandidates.size(); rank++) {
                String name = rankedCandidates.get(rank);
                CandidateTally tally = talliesByName.get(name);
                if (tally == null) {
                    tally = new CandidateTally(name);
                    talliesByName.put(name, tally);
                }
                tally.recordVote(rank, ballotIndex);
            }
        }

        List<CandidateTally> tallies = new ArrayList<>(talliesByName.values());
        tallies.sort(new Comparator<CandidateTally>() {
            @Override
            public int compare(CandidateTally a, CandidateTally b) {
                if (a.getTotalPoints() != b.getTotalPoints()) {
                    return Integer.compare(b.getTotalPoints(), a.getTotalPoints());
                }
                return tieBreakStrategy.compare(a, b);
            }
        });

        List<String> result = new ArrayList<>();
        for (CandidateTally tally : tallies) {
            result.add(tally.getName());
        }
        return result;
    }
}
