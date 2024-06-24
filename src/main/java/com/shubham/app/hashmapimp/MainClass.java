package com.shubham.app.hashmapimp;

public class MainClass {

    public static void main(String[] args) {
        CustomHashMap<Integer, String> customHashMap = new CustomHashMap<>(null);

        customHashMap.put(1, "Hello Shubham");
        customHashMap.put(2, "Hi All");
        customHashMap.put(12, "Okay more");
        customHashMap.put(121, "Okay more");
        customHashMap.put(1226, "testing");
        customHashMap.put(12361, "again");
        // customHashMap

        System.out.println("customHashMap : " + customHashMap);
    }
}
