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
                new Elevator(2, 0, 10, 7, new IdleState()), new Elevator(3, 0, 10, 9, new IdleState()));

        ElevatorController controller = new ElevatorController(elevators, new NearestElevatorDispatchStrategy());
        System.out.println(
                "Someone is about to request an elevator from floor 3 to go up. The elevators are currently at floors 0, 7, and 9.");
        // Hall call: someone on floor 3 wants to go up — |0-3|=3 is strictly nearer
        // than elevator 2's |7-3|=4 or elevator 3's |9-3|=6, so elevator 1 is
        // dispatched
        controller.requestElevator(3, Direction.UP);
        printState(controller);

        for (int i = 0; i < 3; i++) {
            controller.step();
            printState(controller);
        }

        System.out.println("The elevators are currently at floors 3, 7, and 9.");

        // Cabin call: once inside elevator 1, the passenger picks floor 7
        controller.selectDestinationFloor(1, 7);

        for (int i = 0; i < 5; i++) {
            controller.step();
            printState(controller);
        }
    }

    private static void printState(ElevatorController controller) {
        System.out.println("");
        controller.getElevators().forEach(System.out::println);
    }
}
