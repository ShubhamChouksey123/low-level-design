package com.shubham.app.elevatorsystem.service;

import com.shubham.app.elevatorsystem.entity.Direction;
import com.shubham.app.elevatorsystem.entity.DispatchStrategy;
import com.shubham.app.elevatorsystem.entity.Elevator;
import com.shubham.app.elevatorsystem.exception.InvalidFloorException;

import java.util.List;

public class ElevatorController {

    private final List<Elevator> elevators;
    private final DispatchStrategy dispatchStrategy;

    public ElevatorController(List<Elevator> elevators, DispatchStrategy dispatchStrategy) {
        this.elevators = elevators;
        this.dispatchStrategy = dispatchStrategy;
    }

    public void requestElevator(int floor, Direction direction) {
        Elevator elevator = dispatchStrategy.chooseElevator(elevators, floor, direction);
        elevator.addRequest(floor);
    }

    public void selectDestinationFloor(int elevatorId, int floor) {
        Elevator elevator = elevators.stream().filter(e -> e.getId() == elevatorId).findFirst()
                .orElseThrow(() -> new InvalidFloorException("no elevator with id " + elevatorId));
        elevator.addRequest(floor);
    }

    public void step() {
        elevators.forEach(Elevator::step);
    }

    public List<Elevator> getElevators() {
        return List.copyOf(elevators);
    }
}
