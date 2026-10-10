package com.shubham.app.votingalgorithm.practice.service;

import com.shubham.app.votingalgorithm.practice.entity.CandidateTally;
import com.shubham.app.votingalgorithm.practice.entity.TieBreakerStrategy;

import static com.shubham.app.votingalgorithm.practice.entity.CandidateTally.BALLOT_LIST_SIZE;

public class RankPositionTieBreakStrategy implements TieBreakerStrategy {

    @Override
    public int sortLogic(CandidateTally a, CandidateTally b) {
        for(int i = 0 ; i < BALLOT_LIST_SIZE ; i++){
            if(a.getPositionCounts()[i] != b.getPositionCounts()[i]){
                return Integer.compare(b.getPositionCounts()[i], a.getPositionCounts()[i]);
            }
        }
        return a.getName().compareTo(b.getName());
    }
}
