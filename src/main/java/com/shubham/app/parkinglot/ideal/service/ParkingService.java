package com.shubham.app.parkinglot.ideal.service;

import com.shubham.app.parkinglot.ideal.entity.ParkingLot;
import com.shubham.app.parkinglot.ideal.entity.Spot;
import com.shubham.app.parkinglot.ideal.entity.Ticket;
import com.shubham.app.parkinglot.ideal.exception.InvalidTicketException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingService {

    private final ParkingLot parkingLot;
    private final Map<Integer, Ticket> tickets = new ConcurrentHashMap<>();
    private final AtomicInteger nextTicketId = new AtomicInteger(0);

    public ParkingService(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Ticket issueTicket(String vehicleNumber) {
        Spot spot = parkingLot.claimVacantSpot();
        Ticket ticket = new Ticket(nextTicketId.getAndIncrement(), vehicleNumber, spot);
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public void vacateSpot(int ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException("no such ticket: " + ticketId);
        }

        // Ticket.close() throws if this ticket was already used to vacate — the fix for
        // the
        // practice attempt's silent double-vacate bug (see
        // practice/session-02-parking-lot.md).
        ticket.close();
        parkingLot.releaseSpot(ticket.getSpot());
    }
}
