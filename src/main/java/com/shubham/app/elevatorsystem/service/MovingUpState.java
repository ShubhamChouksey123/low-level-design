package com.shubham.app.elevatorsystem.service;

import com.shubham.app.elevatorsystem.entity.Elevator;
import com.shubham.app.elevatorsystem.entity.ElevatorState;

public class MovingUpState implements ElevatorState {

    @Override
    public void addRequest(Elevator elevator, int floor) {
        if (floor >= elevator.getCurrentFloor()) {
            elevator.scheduleUpStop(floor);
        } else {
            elevator.scheduleDownStop(floor);
        }
    }

    @Override
    public void step(Elevator elevator) {
        elevator.moveOneFloorUp();
        elevator.serveCurrentFloorIfRequested();

        if (!elevator.hasUpStops()) {
            elevator.setState(elevator.hasDownStops() ? new MovingDownState() : new IdleState());
        }
    }
}
