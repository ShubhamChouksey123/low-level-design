package com.shubham.app.elevatorsystem.service;

import com.shubham.app.elevatorsystem.entity.Direction;
import com.shubham.app.elevatorsystem.entity.DispatchStrategy;
import com.shubham.app.elevatorsystem.entity.Elevator;

import java.util.Comparator;
import java.util.List;

public class NearestElevatorDispatchStrategy implements DispatchStrategy {

    @Override
    public Elevator chooseElevator(List<Elevator> elevators, int floor, Direction direction) {
        if (elevators.isEmpty()) {
            throw new IllegalStateException("no elevators available to dispatch");
        }

        // direction is currently unused by this policy — a smarter strategy could
        // prefer an
        // elevator already moving toward `floor` in the same `direction` over a
        // merely-nearer one.
        return elevators.stream().min(Comparator.comparingInt(elevator -> Math.abs(elevator.getCurrentFloor() - floor)))
                .orElseThrow();
    }
}
