package com.shubham.app.elevatorsystem.service;

import com.shubham.app.elevatorsystem.entity.Elevator;
import com.shubham.app.elevatorsystem.entity.ElevatorState;

public class IdleState implements ElevatorState {

    @Override
    public void addRequest(Elevator elevator, int floor) {
        if (floor > elevator.getCurrentFloor()) {
            elevator.scheduleUpStop(floor);
            elevator.setState(new MovingUpState());
        } else {
            elevator.scheduleDownStop(floor);
            elevator.setState(new MovingDownState());
        }
    }

    @Override
    public void step(Elevator elevator) {
        // idle elevator has no pending requests by invariant — nothing to do
    }
}
