package com.shubham.app.votingalgorithm.practice.entity;

public interface TieBreakerStrategy {
    int sortLogic(CandidateTally a, CandidateTally b);
}
