package com.shubham.app.votingalgorithm.ideal.entity;

/**
 * One candidate's accumulated state across every processed ballot: total
 * points, how many times they were ranked 1st/2nd/3rd (for
 * {@code RankPositionTieBreakStrategy}), and the index of the last ballot that
 * gave them any points at all.
 *
 * <p>
 * That last field is the actual fix for the practice code's incomplete "whoever
 * reached their point total first" strategy: since points only ever accumulate
 * (never decrease), a candidate's final total is reached exactly when they're
 * last awarded any points - the ballot index at which that happens is
 * sufficient to compare "who reached their tied total earlier," with no need to
 * record a full score-over-time history.
 */
public class CandidateTally {

    private static final int RANK_POSITIONS = 3;

    private final String name;
    private final int[] positionCounts = new int[RANK_POSITIONS];
    private int totalPoints;
    private int lastContributingBallotIndex;

    public CandidateTally(String name) {
        this.name = name;
    }

    void recordVote(int rank, int ballotIndex) {
        int points = RANK_POSITIONS - rank;
        totalPoints += points;
        positionCounts[rank]++;
        lastContributingBallotIndex = ballotIndex;
    }

    public String getName() {
        return name;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getPositionCount(int rank) {
        return positionCounts[rank];
    }

    public int getLastContributingBallotIndex() {
        return lastContributingBallotIndex;
    }
}
