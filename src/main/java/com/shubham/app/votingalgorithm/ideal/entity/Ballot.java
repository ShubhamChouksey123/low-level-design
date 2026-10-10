package com.shubham.app.votingalgorithm.ideal.entity;

import com.shubham.app.votingalgorithm.ideal.exception.InvalidBallotException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One voter's ranked candidates, best to worst. A voter may rank 1, 2, or 3
 * candidates - "up to three different candidates," per the problem statement -
 * not always exactly three, which is the assumption the practice code's fixed
 * {@code for (i = 0; i < 3; i++)} loop made (and crashed on any shorter ballot
 * as a result).
 */
public class Ballot {

    private static final int MAX_RANKED_CANDIDATES = 3;

    private final List<String> rankedCandidates;

    public Ballot(List<String> rankedCandidates) {
        if (rankedCandidates == null || rankedCandidates.isEmpty()) {
            throw new InvalidBallotException("a ballot must rank at least one candidate");
        }
        if (rankedCandidates.size() > MAX_RANKED_CANDIDATES) {
            throw new InvalidBallotException(
                    "a ballot may rank at most " + MAX_RANKED_CANDIDATES + " candidates: " + rankedCandidates);
        }

        Set<String> seen = new HashSet<>();
        for (String candidate : rankedCandidates) {
            if (candidate == null || candidate.isBlank()) {
                throw new InvalidBallotException("a ranked candidate must not be null/blank: " + rankedCandidates);
            }
            if (!seen.add(candidate)) {
                throw new InvalidBallotException("a ballot cannot rank the same candidate twice: " + candidate);
            }
        }

        this.rankedCandidates = Collections.unmodifiableList(rankedCandidates);
    }

    public List<String> getRankedCandidates() {
        return rankedCandidates;
    }
}
