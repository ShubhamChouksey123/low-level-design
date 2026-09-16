package com.shubham.app.snakeladder2.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakeladder2.ideal.entity.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerRegistrationServiceTest {

    @Test
    void assignsSequentialIdsInRegistrationOrder() {
        List<Player> players = new PlayerRegistrationService().registerPlayers(List.of("Shubham", "Ram"));

        assertEquals(2, players.size());
        assertEquals(0, players.get(0).getId());
        assertEquals("Shubham", players.get(0).getName());
        assertEquals(1, players.get(1).getId());
        assertEquals("Ram", players.get(1).getName());
    }
}
