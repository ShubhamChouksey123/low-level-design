package com.shubham.app.votingalgorithm.ideal;

import com.shubham.app.votingalgorithm.ideal.entity.Ballot;
import com.shubham.app.votingalgorithm.ideal.entity.VotingService;
import com.shubham.app.votingalgorithm.ideal.service.EarliestToReachTieBreakStrategy;
import com.shubham.app.votingalgorithm.ideal.service.RankPositionTieBreakStrategy;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Base case, no tie (ballots = [[A,B,C],[B,A,C],[C,A,B]]):");
        List<Ballot> baseBallots = new ArrayList<>();
        baseBallots.add(new Ballot(List.of("A", "B", "C")));
        baseBallots.add(new Ballot(List.of("B", "A", "C")));
        baseBallots.add(new Ballot(List.of("C", "A", "B")));
        VotingService rankPositionService = new VotingService(new RankPositionTieBreakStrategy());
        System.out.println("  " + rankPositionService.getResults(baseBallots));

        System.out.println("\nSingle-candidate ballots (ballots = [[A],[B]]) - the practice code crashed on this:");
        List<Ballot> singleCandidateBallots = new ArrayList<>();
        singleCandidateBallots.add(new Ballot(List.of("A")));
        singleCandidateBallots.add(new Ballot(List.of("B")));
        VotingService earliestToReachService = new VotingService(new EarliestToReachTieBreakStrategy());
        System.out.println("  " + earliestToReachService.getResults(singleCandidateBallots));

        System.out.println("\nSame ballots, two strategies that genuinely disagree (A and B both tie at 3 points):");
        List<Ballot> tieBallots = new ArrayList<>();
        tieBallots.add(new Ballot(List.of("X", "B", "Y"))); // B's 1st contribution (2 pts), index 0
        tieBallots.add(new Ballot(List.of("X", "Y", "B"))); // B's 2nd (last) contribution (1 pt), index 1 -> B=3
        tieBallots.add(new Ballot(List.of("A", "X", "Y"))); // A's only contribution (3 pts), index 2 -> A=3
        System.out.println("  RankPositionTieBreakStrategy:    " + rankPositionService.getResults(tieBallots)
                + " (A above B: more 1st-place votes)");
        System.out.println("  EarliestToReachTieBreakStrategy: " + earliestToReachService.getResults(tieBallots)
                + " (B above A: B's score was locked in earlier, at index 1 vs A's index 2)");
    }
}
