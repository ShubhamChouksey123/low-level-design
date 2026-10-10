package com.shubham.app.votingalgorithm.ideal.service;

import com.shubham.app.votingalgorithm.ideal.entity.CandidateTally;
import com.shubham.app.votingalgorithm.ideal.entity.TieBreakStrategy;

/**
 * Strategy 2: compare candidates by how many ballots ranked them 1st; if still
 * tied, by 2nd; then 3rd. If every position count also ties, falls back to
 * alphabetical order - a deliberate, documented choice for a fully-symmetric
 * tie, not a silent/arbitrary one.
 */
public class RankPositionTieBreakStrategy implements TieBreakStrategy {

    @Override
    public int compare(CandidateTally a, CandidateTally b) {
        for (int rank = 0; rank < 3; rank++) {
            if (a.getPositionCount(rank) != b.getPositionCount(rank)) {
                return Integer.compare(b.getPositionCount(rank), a.getPositionCount(rank));
            }
        }
        return a.getName().compareTo(b.getName());
    }
}
