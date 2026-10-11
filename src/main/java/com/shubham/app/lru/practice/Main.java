package com.shubham.app.lru.practice;

import com.shubham.app.lru.practice.entity.Lru;

public class Main {

    public static void main(String[] args) {
        Lru<Integer, Integer> lru = new Lru<>(3);
        lru.add(1, 1);
        lru.add(2, 2);
        System.out.println("get 2 : " + lru.get(2));
        lru.add(3, 3);
        lru.add(4, 4);
        /** <4> -> <3> -> <2> */
        System.out.println("get 1 : " + lru.get(1));
        System.out.println("get 2 : " + lru.get(2));

        /** <2> -> <4> -> <3> */
        System.out.println("get 4 : " + lru.get(4));

        /** <4> -> <2> -> <3> */
        lru.add(6, 6);
        /** <6> -> <4> -> <2> */
        System.out.println("get 3 : " + lru.get(3));
    }
}
