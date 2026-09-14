package com.shubham.app.elevatorsystem.entity;

public interface ElevatorState {

    void addRequest(Elevator elevator, int floor);

    void step(Elevator elevator);
}
