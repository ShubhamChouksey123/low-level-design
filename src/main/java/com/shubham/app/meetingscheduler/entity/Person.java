package com.shubham.app.meetingscheduler.entity;

public class Person {

    private Integer personId;
    private String name;

    public Person(Integer personId, String name) {
        this.personId = personId;
        this.name = name;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" + "personId=" + personId + ", name='" + name + '\'' + '}';
    }
}
