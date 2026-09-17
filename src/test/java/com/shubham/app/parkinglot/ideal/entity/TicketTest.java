package com.shubham.app.parkinglot.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.parkinglot.ideal.exception.InvalidTicketException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketTest {

    @Test
    void closingAnOpenTicketSucceeds() {
        Ticket ticket = new Ticket(0, "KA-01-AA-1111", new Spot(0));

        assertDoesNotThrow(ticket::close);

        assertTrue(ticket.isClosed());
    }

    @Test
    void closingAnAlreadyClosedTicketThrows() {
        Ticket ticket = new Ticket(0, "KA-01-AA-1111", new Spot(0));
        ticket.close();

        assertThrows(InvalidTicketException.class, ticket::close);
    }
}
