package com.shubham.app.elevatorsystem;

import com.shubham.app.elevatorsystem.entity.Direction;
import com.shubham.app.elevatorsystem.entity.Elevator;
import com.shubham.app.elevatorsystem.service.ElevatorController;
import com.shubham.app.elevatorsystem.service.IdleState;
import com.shubham.app.elevatorsystem.service.NearestElevatorDispatchStrategy;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Elevator> elevators = List.of(new Elevator(1, 0, 10, 0, new IdleState()),
                new Elevator(2, 0, 10, 5, new IdleState()), new Elevator(3, 0, 10, 9, new IdleState()));

        ElevatorController controller = new ElevatorController(elevators, new NearestElevatorDispatchStrategy());

        // Hall call: someone on floor 3 wants to go up — nearest elevator (id 1, at
        // floor 0) is
        // dispatched
        controller.requestElevator(3, Direction.UP);
        printState(controller);

        for (int i = 0; i < 3; i++) {
            controller.step();
            printState(controller);
        }

        // Cabin call: once inside elevator 1, the passenger picks floor 7
        controller.selectDestinationFloor(1, 7);

        for (int i = 0; i < 5; i++) {
            controller.step();
            printState(controller);
        }
    }

    private static void printState(ElevatorController controller) {
        controller.getElevators().forEach(System.out::println);
    }
}
