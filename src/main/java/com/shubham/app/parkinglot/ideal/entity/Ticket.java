package com.shubham.app.parkinglot.ideal.entity;

import com.shubham.app.parkinglot.ideal.exception.InvalidTicketException;

import java.time.Instant;

public class Ticket {

    private final int id;
    private final String vehicleNumber;
    private final Spot spot;
    private final Instant issuedAt;
    private boolean closed;

    public Ticket(int id, String vehicleNumber, Spot spot) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.spot = spot;
        this.issuedAt = Instant.now();
        this.closed = false;
    }

    public int getId() {
        return id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Spot getSpot() {
        return spot;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public boolean isClosed() {
        return closed;
    }

    // A ticket is a single-use claim on a spot: closing it is the fix for the
    // practice
    // attempt's bug, where vacating with the same ticket twice silently freed
    // whichever
    // vehicle now occupied that spot. Closing an already-closed ticket throws
    // instead.
    public synchronized void close() {
        if (closed) {
            throw new InvalidTicketException("ticket " + id + " was already used to vacate a spot");
        }
        closed = true;
    }

    @Override
    public String toString() {
        return "Ticket{id=" + id + ", vehicleNumber='" + vehicleNumber + "', spot=" + spot + ", closed=" + closed + '}';
    }
}
