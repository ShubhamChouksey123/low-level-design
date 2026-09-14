package com.shubham.app.elevatorsystem.entity;

import java.util.List;

public interface DispatchStrategy {

    Elevator chooseElevator(List<Elevator> elevators, int floor, Direction direction);
}
