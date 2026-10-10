package com.shubham.app.subscriptionbilling.ideal.entity;

import com.shubham.app.subscriptionbilling.ideal.exception.InvalidUsageException;

import java.time.YearMonth;

/**
 * How many days a single product was used by a single user in a single calendar
 * month. {@code
 * period} is a {@link YearMonth}, not a bare {@code int month} - it already
 * knows how many days are actually in that month
 * ({@link YearMonth#lengthOfMonth()}, 28/29/30/31), so proration never has to
 * guess or hardcode a days-per-month constant.
 */
public class UsageRecord {

    private final int productId;
    private final YearMonth period;
    private final int daysUsed;

    public UsageRecord(int productId, YearMonth period, int daysUsed) {
        if (period == null) {
            throw new InvalidUsageException("period must not be null");
        }
        if (daysUsed < 0 || daysUsed > period.lengthOfMonth()) {
            throw new InvalidUsageException(
                    "daysUsed must be between 0 and " + period.lengthOfMonth() + " for " + period + ": " + daysUsed);
        }
        this.productId = productId;
        this.period = period;
        this.daysUsed = daysUsed;
    }

    public int getProductId() {
        return productId;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public int getDaysUsed() {
        return daysUsed;
    }
}
