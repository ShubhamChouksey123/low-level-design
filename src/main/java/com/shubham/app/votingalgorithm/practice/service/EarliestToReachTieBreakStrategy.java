package com.shubham.app.votingalgorithm.practice.service;

import com.shubham.app.votingalgorithm.practice.entity.CandidateTally;
import com.shubham.app.votingalgorithm.practice.entity.TieBreakerStrategy;

public class EarliestToReachTieBreakStrategy implements TieBreakerStrategy {

    @Override
    public int sortLogic(CandidateTally a, CandidateTally b){
        return Integer.compare(a.getLastBallotIndex(), b.getLastBallotIndex());
    }
}
