package com.shubham.app.parkinglot.ideal;

import com.shubham.app.parkinglot.ideal.entity.ParkingLot;
import com.shubham.app.parkinglot.ideal.entity.Ticket;
import com.shubham.app.parkinglot.ideal.exception.InvalidTicketException;
import com.shubham.app.parkinglot.ideal.exception.ParkingFullException;
import com.shubham.app.parkinglot.ideal.service.FirstAvailableSpotAllocationStrategy;
import com.shubham.app.parkinglot.ideal.service.ParkingLotCreationService;
import com.shubham.app.parkinglot.ideal.service.ParkingService;

public class Main {

    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLotCreationService().createParkingLot(3,
                new FirstAvailableSpotAllocationStrategy());
        ParkingService parkingService = new ParkingService(parkingLot);

        Ticket first = parkingService.issueTicket("KA-01-AA-1111");
        System.out.println("Issued: " + first);

        parkingService.vacateSpot(first.getId());
        System.out.println("Vacated spot " + first.getSpot().getId() + " for ticket " + first.getId());

        // First-available reuses the just-freed spot for the next vehicle.
        Ticket second = parkingService.issueTicket("KA-01-BB-2222");
        System.out.println("Issued: " + second);

        // This is the practice attempt's bug, fixed: replaying the first (already-used)
        // ticket must NOT free the second vehicle's spot out from under it.
        try {
            parkingService.vacateSpot(first.getId());
        } catch (InvalidTicketException e) {
            System.out.println("Rejected stale ticket replay: " + e.getMessage());
        }

        parkingService.issueTicket("KA-01-CC-3333");
        parkingService.issueTicket("KA-01-DD-4444");

        try {
            parkingService.issueTicket("KA-01-EE-5555");
        } catch (ParkingFullException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
