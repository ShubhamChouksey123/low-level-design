package com.shubham.app.elevatorsystem.service;

import com.shubham.app.elevatorsystem.entity.Elevator;
import com.shubham.app.elevatorsystem.entity.ElevatorState;

public class MovingDownState implements ElevatorState {

    @Override
    public void addRequest(Elevator elevator, int floor) {
        if (floor <= elevator.getCurrentFloor()) {
            elevator.scheduleDownStop(floor);
        } else {
            elevator.scheduleUpStop(floor);
        }
    }

    @Override
    public void step(Elevator elevator) {
        elevator.moveOneFloorDown();
        elevator.serveCurrentFloorIfRequested();

        if (!elevator.hasDownStops()) {
            elevator.setState(elevator.hasUpStops() ? new MovingUpState() : new IdleState());
        }
    }
}
