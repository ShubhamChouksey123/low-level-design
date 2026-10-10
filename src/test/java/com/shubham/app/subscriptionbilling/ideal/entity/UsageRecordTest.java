package com.shubham.app.subscriptionbilling.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal.exception.InvalidUsageException;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UsageRecordTest {

    @Test
    void constructorRejectsANullPeriod() {
        assertThrows(InvalidUsageException.class, () -> new UsageRecord(1, null, 10));
    }

    @Test
    void constructorRejectsNegativeDaysUsed() {
        assertThrows(InvalidUsageException.class, () -> new UsageRecord(1, YearMonth.of(2025, 4), -1));
    }

    // April has 30 days - 31 days of usage in it is impossible and must be
    // rejected, not silently
    // accepted the way the practice code's addUsage(usedInDays < 0) check alone
    // would allow.
    @Test
    void constructorRejectsDaysUsedBeyondTheActualLengthOfThatMonth() {
        assertThrows(InvalidUsageException.class, () -> new UsageRecord(1, YearMonth.of(2025, 4), 31));
    }

    @Test
    void constructorAllowsDaysUsedUpToTheExactLengthOfThatMonth() {
        new UsageRecord(1, YearMonth.of(2025, 4), 30);
        new UsageRecord(1, YearMonth.of(2024, 2), 29);
    }
}
