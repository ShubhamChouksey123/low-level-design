package com.shubham.app.subscriptionbilling.ideal.entity;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsageRepositoryTest {

    @Test
    void getUsageReturnsEmptyForAUserOrPeriodNeverRecorded() {
        UsageRepository repository = new UsageRepository();
        assertTrue(repository.getUsage(1, YearMonth.of(2025, 4)).isEmpty());

        repository.recordUsage(1, new UsageRecord(1, YearMonth.of(2025, 4), 10));
        assertTrue(repository.getUsage(1, YearMonth.of(2025, 5)).isEmpty());
        assertTrue(repository.getUsage(2, YearMonth.of(2025, 4)).isEmpty());
    }

    // The practice code's UsageService.addUsage just appended, so two usage entries
    // for the same
    // product in the same month would both be summed by the billing step - silently
    // double
    // billing that product. recordUsage replaces instead.
    @Test
    void recordingUsageTwiceForTheSameProductAndPeriodReplacesRatherThanAccumulates() {
        UsageRepository repository = new UsageRepository();
        YearMonth april = YearMonth.of(2025, 4);

        repository.recordUsage(1, new UsageRecord(1, april, 10));
        repository.recordUsage(1, new UsageRecord(1, april, 20));

        List<UsageRecord> usage = repository.getUsage(1, april);
        assertEquals(1, usage.size());
        assertEquals(20, usage.get(0).getDaysUsed());
    }

    @Test
    void usageForDifferentProductsInTheSamePeriodBothRemain() {
        UsageRepository repository = new UsageRepository();
        YearMonth april = YearMonth.of(2025, 4);

        repository.recordUsage(1, new UsageRecord(1, april, 10));
        repository.recordUsage(1, new UsageRecord(2, april, 5));

        assertEquals(2, repository.getUsage(1, april).size());
    }
}
