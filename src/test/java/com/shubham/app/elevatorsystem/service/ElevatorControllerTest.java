package com.shubham.app.elevatorsystem.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.elevatorsystem.entity.Direction;
import com.shubham.app.elevatorsystem.entity.Elevator;
import com.shubham.app.elevatorsystem.exception.InvalidFloorException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElevatorControllerTest {

    @Test
    void nearestElevatorIsDispatchedForAHallCall() {
        Elevator e1 = new Elevator(1, 0, 20, 0, new IdleState());
        Elevator e2 = new Elevator(2, 0, 20, 5, new IdleState());
        Elevator e3 = new Elevator(3, 0, 20, 9, new IdleState());
        ElevatorController controller = new ElevatorController(List.of(e1, e2, e3),
                new NearestElevatorDispatchStrategy());

        controller.requestElevator(6, Direction.UP); // closest to e2 (floor 5)

        assertTrue(e2.hasUpStops());
        assertTrue(e1.getUpStopsSnapshot().isEmpty());
        assertTrue(e3.getUpStopsSnapshot().isEmpty());
    }

    @Test
    void hallCallForFloorOutsideRangeThrows() {
        Elevator e1 = new Elevator(1, 0, 20, 0, new IdleState());
        ElevatorController controller = new ElevatorController(List.of(e1), new NearestElevatorDispatchStrategy());

        assertThrows(InvalidFloorException.class, () -> controller.requestElevator(100, Direction.UP));
    }

    @Test
    void cabinCallForUnknownElevatorIdThrows() {
        Elevator e1 = new Elevator(1, 0, 20, 0, new IdleState());
        ElevatorController controller = new ElevatorController(List.of(e1), new NearestElevatorDispatchStrategy());

        assertThrows(InvalidFloorException.class, () -> controller.selectDestinationFloor(999, 5));
    }

    @Test
    void concurrentHallCallsAreAllEventuallyServedWithoutLoss() throws InterruptedException {
        Elevator elevator = new Elevator(1, 0, 50, 0, new IdleState());
        ElevatorController controller = new ElevatorController(List.of(elevator),
                new NearestElevatorDispatchStrategy());

        List<Integer> requestedFloors = IntStream.rangeClosed(1, 20).boxed().collect(Collectors.toList());

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int floor : requestedFloors) {
            executor.submit(() -> controller.requestElevator(floor, Direction.UP));
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        int maxSteps = 200;
        while ((elevator.hasUpStops() || elevator.hasDownStops()) && maxSteps-- > 0) {
            controller.step();
        }

        assertEquals(Set.copyOf(requestedFloors), Set.copyOf(elevator.getServedFloors()));
    }
}
