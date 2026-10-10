package com.shubham.app.votingalgorithm.practice.entity;

import java.util.List;

public class Ballot {

    private List<String> rankedCandidates;

    public Ballot(List<String> rankedCandidates) {
        this.rankedCandidates = rankedCandidates;
    }

    public List<String> getRankedCandidates() {
        return rankedCandidates;
    }
}
