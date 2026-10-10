package com.shubham.app.votingalgorithm.practice.entity;

public class CandidateTally {

    public static final int BALLOT_LIST_SIZE = 3;

    private String name;
    private int[] positionCounts;
    private int lastBallotIndex;
    private int score;

    public void addVote(int rank, int index) {
        int point = BALLOT_LIST_SIZE - rank;
        score += (point);
        lastBallotIndex = index;
        positionCounts[rank]++;
    }

    public CandidateTally(String name) {
        this.name = name;
        this.positionCounts = new int[BALLOT_LIST_SIZE];
        this.lastBallotIndex = Integer.MAX_VALUE;
        this.score = 0;
    }

    public int[] getPositionCounts() {
        return positionCounts;
    }

    public int getLastBallotIndex() {
        return lastBallotIndex;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
