package com.shubham.app.concurrencyprimitives.volatiledemo;

// Deliberately demonstrates the ❌ common mistake named in
// concepts/concurrency-primitives.md: volatile guarantees every thread sees
// the latest value, but count++ is read-modify-write - three separate steps -
// and volatile does nothing to make those three steps atomic as a unit.
public class VolatileCounter {

    private volatile int count = 0;

    // NOT atomic: read count, add 1, write count - a race can land between any two
    // of these steps.
    public void increment() {
        count++;
    }

    public int get() {
        return count;
    }
}
