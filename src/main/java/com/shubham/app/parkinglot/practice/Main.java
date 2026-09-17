package com.shubham.app.parkinglot.practice;

import com.shubham.app.parkinglot.practice.entity.ParkingLot;
import com.shubham.app.parkinglot.practice.service.ParkingLotCreation;
import com.shubham.app.parkinglot.practice.service.ParkingService;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Random random = new Random();

        ParkingLotCreation parkingLotCreation = new ParkingLotCreation();
        ParkingLot parkingLot = parkingLotCreation.createParking(100);

        ParkingService parkingService = new ParkingService(parkingLot);

        int ticketNumber = parkingService.issueTicket("MAP1910");
        parkingService.vacateSpot(ticketNumber);

        for (int i = 0; i < 120; i++) {
            int vehicleNumber = 1 + random.nextInt(10000);
            try {
                parkingService.issueTicket(String.valueOf("AP" + vehicleNumber));
            } catch (Exception e) {
                System.out.println("e : " + e.getMessage());
            }
        }
    }
}
