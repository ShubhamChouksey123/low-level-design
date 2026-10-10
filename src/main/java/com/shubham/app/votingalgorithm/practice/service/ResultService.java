package com.shubham.app.votingalgorithm.practice.service;

import com.shubham.app.votingalgorithm.practice.entity.Ballot;
import com.shubham.app.votingalgorithm.practice.entity.CandidateTally;
import com.shubham.app.votingalgorithm.practice.entity.TieBreakerStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultService {

    private TieBreakerStrategy tieBreakerStrategy;

    public ResultService(TieBreakerStrategy tieBreakerStrategy) {
        this.tieBreakerStrategy = tieBreakerStrategy;
    }

    public List<String> getResults(List<Ballot> ballots) {
        Map<String, CandidateTally> voteCount = new HashMap<>();

        for (int ballotIndex = 0 ; ballotIndex < ballots.size() ; ballotIndex++) {
            Ballot ballot = ballots.get(ballotIndex);
            for(int i = 0 ; i < ballot.getRankedCandidates().size() ; i++){
                String name = ballot.getRankedCandidates().get(i);
                CandidateTally tally = voteCount.get(name);
                if(tally == null){
                    tally = new CandidateTally(name);
                    voteCount.put(name, tally);
                }
                tally.addVote(i, ballotIndex);
            }
        }

        List<CandidateTally> teams = new ArrayList<>(voteCount.values());

        Collections.sort(teams, (a, b) -> {
            if(a.getScore() != b.getScore()){
                return Integer.compare(b.getScore(), a.getScore());
            }
            return tieBreakerStrategy.sortLogic(a, b);
        });

        List<String> ans = new ArrayList<>();
        for(CandidateTally candidateTally : teams){
            ans.add(candidateTally.getName());
        }
        return ans;
    }
}
