package com.shubham.app.votingalgorithm.ideal.service;

import com.shubham.app.votingalgorithm.ideal.entity.CandidateTally;
import com.shubham.app.votingalgorithm.ideal.entity.TieBreakStrategy;

/**
 * Strategy 1: whoever reached their (tied) point total first wins. Since points
 * only ever accumulate, a candidate's final total is reached exactly when
 * they're last awarded any points - so comparing
 * {@code lastContributingBallotIndex} (smaller = earlier) is sufficient; no
 * score-over-time history needs to be recorded. This is the fix for the
 * practice code's {@code //
 * TODO : fix this} - the earlier version had no timestamp data to compare at
 * all.
 *
 * <p>
 * If two candidates' last contributing ballot is the exact same index (they
 * were both decided by the same ballot), falls back to alphabetical order - the
 * ambiguity the problem's source itself left unresolved, now given an explicit,
 * deterministic answer.
 */
public class EarliestToReachTieBreakStrategy implements TieBreakStrategy {

    @Override
    public int compare(CandidateTally a, CandidateTally b) {
        if (a.getLastContributingBallotIndex() != b.getLastContributingBallotIndex()) {
            return Integer.compare(a.getLastContributingBallotIndex(), b.getLastContributingBallotIndex());
        }
        return a.getName().compareTo(b.getName());
    }
}
