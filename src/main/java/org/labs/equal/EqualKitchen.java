package org.labs.equal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.labs.Kitchen;
import static org.labs.Config.KITCHEN_WAIT_TIME_MS;

public class EqualKitchen extends Kitchen {
    private final int totalProgrammers;
    private final ConcurrentHashMap<Integer, Object> servedProgrammers;
    private final ReentrantLock lock = new ReentrantLock(true);

    public EqualKitchen(int totalPortions, int totalProgrammers) {
        super(totalPortions);

        this.totalProgrammers = totalProgrammers;
        this.servedProgrammers = new ConcurrentHashMap<>();
    }

    // False when no portions were left to get, true otherwise
    public Boolean getPortion(int programmerId) throws InterruptedException {
        Thread.sleep(KITCHEN_WAIT_TIME_MS.getBetween());

        lock.lock();
        try {
            if (servedProgrammers.containsKey(programmerId)) {
                return null;
            }
            servedProgrammers.put(programmerId, new Object());
            if (servedProgrammers.size() == totalProgrammers) {
                servedProgrammers.clear();
            }

            return totalPortions.getAndUpdate(p -> p > 0 ? p - 1 : p) > 0;
        } finally {
            lock.unlock();
        }
    }
}
