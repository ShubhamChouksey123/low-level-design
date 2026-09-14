package com.shubham.app.elevatorsystem.entity;

import com.shubham.app.elevatorsystem.exception.InvalidFloorException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Elevator {

    private final int id;
    private final int minFloor;
    private final int maxFloor;

    private int currentFloor;
    private ElevatorState state;

    private final TreeSet<Integer> upStops = new TreeSet<>();
    private final TreeSet<Integer> downStops = new TreeSet<>(Comparator.reverseOrder());
    private final List<Integer> servedFloors = new ArrayList<>();

    public Elevator(int id, int minFloor, int maxFloor, int startFloor, ElevatorState initialState) {
        this.id = id;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = startFloor;
        this.state = initialState;
    }

    public synchronized void addRequest(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new InvalidFloorException(
                    "floor " + floor + " is outside building range [" + minFloor + ", " + maxFloor + "]");
        }
        if (floor == currentFloor) {
            return;
        }
        state.addRequest(this, floor);
    }

    public synchronized void step() {
        state.step(this);
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public ElevatorState getState() {
        return state;
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }

    public void moveOneFloorUp() {
        currentFloor = Math.min(currentFloor + 1, maxFloor);
    }

    public void moveOneFloorDown() {
        currentFloor = Math.max(currentFloor - 1, minFloor);
    }

    public void scheduleUpStop(int floor) {
        upStops.add(floor);
    }

    public void scheduleDownStop(int floor) {
        downStops.add(floor);
    }

    public boolean hasUpStops() {
        return !upStops.isEmpty();
    }

    public boolean hasDownStops() {
        return !downStops.isEmpty();
    }

    public boolean serveCurrentFloorIfRequested() {
        boolean removed = upStops.remove(currentFloor) || downStops.remove(currentFloor);
        if (removed) {
            servedFloors.add(currentFloor);
        }
        return removed;
    }

    public List<Integer> getServedFloors() {
        return List.copyOf(servedFloors);
    }

    public TreeSet<Integer> getUpStopsSnapshot() {
        return new TreeSet<>(upStops);
    }

    public TreeSet<Integer> getDownStopsSnapshot() {
        return new TreeSet<>(downStops);
    }

    @Override
    public String toString() {
        return "Elevator{" + "id=" + id + ", currentFloor=" + currentFloor + ", state="
                + state.getClass().getSimpleName() + ", upStops=" + upStops + ", downStops=" + downStops + '}';
    }
}
