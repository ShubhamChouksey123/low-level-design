package com.shubham.app.parkinglot.practice.entity;

import java.util.Date;

public class Ticket {

    private int id;
    private Date createdAt;
    private String vehicleNumber;
    private Spot spot;

    public Ticket(int id, Date createdAt, String vehicleNumber, Spot spot) {
        this.id = id;
        this.createdAt = createdAt;
        this.vehicleNumber = vehicleNumber;
        this.spot = spot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

    @Override
    public String toString() {
        return "Ticket{" + "id=" + id + ", createdAt=" + createdAt + ", vehicleNumber='" + vehicleNumber + '\''
                + ", spot=" + spot + '}';
    }
}
