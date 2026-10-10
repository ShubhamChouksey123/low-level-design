package com.shubham.app.subscriptionbilling.ideal2.entity;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Usage indexed by userId, then by billing period. Recording usage for a
 * (userId, productId, period) that already has a record replaces it, rather
 * than adding a second entry - two usage records for the same product in the
 * same month would otherwise silently double-bill it.
 */
public class UsageRepository {

    private final Map<Integer, Map<YearMonth, List<UsageRecord>>> usageByUserThenPeriod = new HashMap<>();

    public void recordUsage(int userId, UsageRecord record) {
        Map<YearMonth, List<UsageRecord>> usageByPeriod = usageByUserThenPeriod.get(userId);
        if (usageByPeriod == null) {
            usageByPeriod = new HashMap<>();
            usageByUserThenPeriod.put(userId, usageByPeriod);
        }

        List<UsageRecord> records = usageByPeriod.get(record.getPeriod());
        if (records == null) {
            records = new ArrayList<>();
            usageByPeriod.put(record.getPeriod(), records);
        }

        Iterator<UsageRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getProductId() == record.getProductId()) {
                iterator.remove();
            }
        }
        records.add(record);
    }

    public List<UsageRecord> getUsage(int userId, YearMonth period) {
        Map<YearMonth, List<UsageRecord>> usageByPeriod = usageByUserThenPeriod.get(userId);
        if (usageByPeriod == null) {
            return Collections.emptyList();
        }
        return usageByPeriod.getOrDefault(period, Collections.emptyList());
    }
}
