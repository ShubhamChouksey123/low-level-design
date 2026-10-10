package com.shubham.app.votingalgorithm.ideal.entity;

/**
 * The Strategy for "how to handle the scenario where candidates have the same
 * points?" - only ever invoked by {@link VotingService} when
 * {@code a.getTotalPoints() == b.getTotalPoints()}. The base rule (sort by
 * total points, descending) is fixed by the problem statement itself and is not
 * part of this Strategy - only what breaks a tie varies.
 */
public interface TieBreakStrategy {

    /**
     * Negative if {@code a} should rank before {@code b}, positive if after, zero
     * if still tied.
     */
    int compare(CandidateTally a, CandidateTally b);
}
