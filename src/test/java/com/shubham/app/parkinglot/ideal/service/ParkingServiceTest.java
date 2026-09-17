package com.shubham.app.parkinglot.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.parkinglot.ideal.entity.ParkingLot;
import com.shubham.app.parkinglot.ideal.entity.Ticket;
import com.shubham.app.parkinglot.ideal.exception.InvalidTicketException;
import com.shubham.app.parkinglot.ideal.exception.ParkingFullException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingServiceTest {

    @Test
    void issuingATicketOccupiesAVacantSpot() {
        ParkingLot lot = new ParkingLotCreationService().createParkingLot(1,
                new FirstAvailableSpotAllocationStrategy());
        ParkingService service = new ParkingService(lot);

        Ticket ticket = service.issueTicket("KA-01-AA-1111");

        assertEquals("KA-01-AA-1111", ticket.getVehicleNumber());
        assertThrows(ParkingFullException.class, () -> service.issueTicket("KA-01-BB-2222"));
    }

    @Test
    void vacatingFreesTheSpotForTheNextVehicle() {
        ParkingLot lot = new ParkingLotCreationService().createParkingLot(1,
                new FirstAvailableSpotAllocationStrategy());
        ParkingService service = new ParkingService(lot);
        Ticket first = service.issueTicket("KA-01-AA-1111");

        service.vacateSpot(first.getId());

        Ticket second = service.issueTicket("KA-01-BB-2222");
        assertEquals(first.getSpot().getId(), second.getSpot().getId());
    }

    @Test
    void vacatingWithAnUnknownTicketIdThrows() {
        ParkingLot lot = new ParkingLotCreationService().createParkingLot(1,
                new FirstAvailableSpotAllocationStrategy());
        ParkingService service = new ParkingService(lot);

        assertThrows(InvalidTicketException.class, () -> service.vacateSpot(999));
    }

    // Regression test for practice/session-02-parking-lot.md's bug: replaying an
    // already-used ticket must NOT free whichever vehicle now occupies that spot.
    @Test
    void replayingAnAlreadyVacatedTicketDoesNotFreeTheNextOccupantsSpot() {
        ParkingLot lot = new ParkingLotCreationService().createParkingLot(1,
                new FirstAvailableSpotAllocationStrategy());
        ParkingService service = new ParkingService(lot);
        Ticket first = service.issueTicket("KA-01-AA-1111");
        service.vacateSpot(first.getId());
        Ticket second = service.issueTicket("KA-01-BB-2222");

        assertThrows(InvalidTicketException.class, () -> service.vacateSpot(first.getId()));

        // second's spot must still be occupied — a third vehicle should not be able to
        // claim it.
        assertThrows(ParkingFullException.class, () -> service.issueTicket("KA-01-CC-3333"));
    }
}
