package com.shubham.app.votingalgorithm.practice;

import com.shubham.app.votingalgorithm.practice.entity.Ballot;
import com.shubham.app.votingalgorithm.practice.service.EarliestToReachTieBreakStrategy;
import com.shubham.app.votingalgorithm.practice.service.RankPositionTieBreakStrategy;
import com.shubham.app.votingalgorithm.practice.service.ResultService;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Ballot> ballots = new ArrayList<>();
        ballots.add(new Ballot(List.of("A", "B", "C")));
        ballots.add(new Ballot(List.of("B", "A", "C")));
        ballots.add(new Ballot(List.of("C", "A", "B")));

        ResultService resultService = new ResultService(new RankPositionTieBreakStrategy());
        System.out.println("resultService : " + resultService.getResults(ballots));

        ResultService resultService2 = new ResultService(new EarliestToReachTieBreakStrategy());
        System.out.println("resultService2 : " + resultService2.getResults(ballots));
    }
}
