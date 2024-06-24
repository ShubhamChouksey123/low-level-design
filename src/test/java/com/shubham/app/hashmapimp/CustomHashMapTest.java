package com.shubham.app.hashmapimp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {

    @Test
    void testCustomHashMap() {
        CustomHashMap<Integer, String> customHashMap = new CustomHashMap<>(null);

        customHashMap.put(1, "Hello Shubham");
        customHashMap.put(2, "Hi All");
        customHashMap.put(12, "Okay more");
        customHashMap.put(121, "Okay more");
        customHashMap.put(1226, "testing");
        customHashMap.put(12361, "again");
        // customHashMap

        System.out.println("customHashMap : " + customHashMap);

        assertNotNull(customHashMap);
        assertEquals("Hello Shubham", customHashMap.get(1));
    }

    @Test
    void testCustomHashMap1() {
        CustomHashMap<Integer, String> customHashMap = new CustomHashMap<>(null);

        customHashMap.put(1, "Hello Shubham");
        customHashMap.put(2, "Hi All");
        customHashMap.put(12, "Okay more");
        customHashMap.put(121, "Okay more");
        customHashMap.put(1226, "testing");
        customHashMap.put(12361, "again");
        customHashMap.put(1, "Hello Shubham changed");
        // customHashMap

        System.out.println("customHashMap : " + customHashMap);

        assertNotNull(customHashMap);
        assertEquals("Hello Shubham changed", customHashMap.get(1));
    }

    @Test
    void testCustomHashMap3() {
        CustomHashMap<Integer, String> customHashMap = new CustomHashMap<>(null);

        customHashMap.put(1, "Hello Shubham");
        customHashMap.put(2, "Hi All");
        customHashMap.put(12, "Okay more");
        customHashMap.put(121, "Okay more");
        customHashMap.put(1226, "testing");
        customHashMap.put(12361, "again");
        customHashMap.put(1, "Hello Shubham changed");
        // customHashMap

        System.out.println("customHashMap : " + customHashMap);

        assertNotNull(customHashMap);
        assertNull(customHashMap.get(100));
    }
}
