package org.labs;

import java.util.concurrent.atomic.AtomicInteger;
import static org.labs.Config.KITCHEN_WAIT_TIME_MS;

public class Kitchen {
    protected final AtomicInteger totalPortions;

    public Kitchen(int totalPortions) {
        assert totalPortions > 0;
        this.totalPortions = new AtomicInteger(totalPortions);
    }

    // False when no portions were left to get, true otherwise
    public Boolean getPortion(int programmerId) throws InterruptedException {
        Thread.sleep(KITCHEN_WAIT_TIME_MS.getBetween());
        return totalPortions.getAndUpdate(p -> p > 0 ? p - 1 : p) > 0;
    }
}
