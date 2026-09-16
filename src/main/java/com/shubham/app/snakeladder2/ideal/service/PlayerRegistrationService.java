package com.shubham.app.snakeladder2.ideal.service;

import com.shubham.app.snakeladder2.ideal.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerRegistrationService {

    public List<Player> registerPlayers(List<String> names) {
        List<Player> players = new ArrayList<>();
        for (int id = 0; id < names.size(); id++) {
            players.add(new Player(id, names.get(id)));
        }
        return players;
    }
}
