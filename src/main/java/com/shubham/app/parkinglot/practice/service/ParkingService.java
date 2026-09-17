package com.shubham.app.parkinglot.practice.service;

import com.shubham.app.parkinglot.practice.entity.ParkingLot;
import com.shubham.app.parkinglot.practice.entity.Spot;
import com.shubham.app.parkinglot.practice.entity.SpotStatus;
import com.shubham.app.parkinglot.practice.entity.Ticket;
import com.shubham.app.parkinglot.practice.exception.InvalidTicketException;
import com.shubham.app.parkinglot.practice.exception.ParkingFullException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ParkingService {

    private ParkingLot parkingLot;
    private List<Ticket> tickets;

    private static int ticketId;

    public ParkingService(ParkingLot parkingLot) {
        this.ticketId = 0;
        this.tickets = new ArrayList<>();
        this.parkingLot = parkingLot;
    }

    public int issueTicket(String vehicleNumber) {

        Optional<Spot> vacantSpot = Optional.empty();
        for (Spot spot : parkingLot.getSpots()) {
            if (spot.getStatus() == SpotStatus.VACANT) {
                vacantSpot = Optional.of(spot);
                break;
            }
        }

        if (vacantSpot.isEmpty()) {
            throw new ParkingFullException("parking is full, come again later");
        }

        vacantSpot.get().setStatus(SpotStatus.OCCUPIED);
        Ticket ticket = new Ticket(ticketId++, new Date(), vehicleNumber, vacantSpot.get());
        tickets.add(ticket);

        System.out.println("Ticket issued : " + ticket);

        return ticket.getId();
    }

    public boolean vacateSpot(int ticketId) {

        Optional<Ticket> userTicket = Optional.empty();
        for (Ticket ticket : tickets) {
            if (ticket.getId() == ticketId) {
                userTicket = Optional.of(ticket);
                break;
            }
        }

        if (userTicket.isEmpty()) {
            throw new InvalidTicketException("ticket is invalid");
        }

        Spot reservedSpot = userTicket.get().getSpot();
        reservedSpot.setStatus(SpotStatus.VACANT);
        System.out.println("Vacant the spot : " + reservedSpot.getId() + " for ticket : " + userTicket);
        return true;
    }
}
