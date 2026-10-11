package com.shubham.app.firstunique.practice;

public class Main {
    public static void main(String[] args) {

        FirstUnique firstUnique = new FirstUnique(new int[]{2, 3, 5});
        System.out.println("unique : " + firstUnique.showFirstUnique());
        firstUnique.add(5);
        System.out.println("unique : " + firstUnique.showFirstUnique());
        firstUnique.add(2);
        System.out.println("unique : " + firstUnique.showFirstUnique());
        firstUnique.add(3);
        System.out.println("unique : " + firstUnique.showFirstUnique());
    }
}
