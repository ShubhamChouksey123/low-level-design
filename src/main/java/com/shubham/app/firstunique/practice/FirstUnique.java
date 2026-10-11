package com.shubham.app.firstunique.practice;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class FirstUnique {

    private LinkedHashSet<Integer> uniques;
    private Set<Integer> seen;

    public FirstUnique(int[] nums) {
        Objects.requireNonNull(nums, "nums must not be null");
        this.uniques = new LinkedHashSet<>();
        this.seen = new HashSet<>();
        for (int num : nums) {
            add(num);
        }
    }

    public int showFirstUnique() {
        if (uniques.isEmpty())
            return -1;

        return uniques.iterator().next();
    }

    public void add(int num) {
        if (!seen.contains(num)) {
            seen.add(num);
            uniques.add(num);
        } else if (uniques.contains(num)) {
            uniques.remove(num);
        }
    }
}
