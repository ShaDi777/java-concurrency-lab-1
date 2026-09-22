package org.labs;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Table {

    public static final int SHUTDOWN_ORDER = -1;

    private final List<Spoon> spoons;

    // The less programmer has eaten - the more first to poll it gets
    private final PriorityBlockingQueue<Programmer> orders;

    // template programmer to finish order processing
    private final Programmer shutdownProgrammer;

    private final AtomicInteger activeProgrammers;
    private final AtomicBoolean active = new AtomicBoolean(true);

    private final int waiterCount;

    public Table(List<Spoon> spoons, int programmerCount, int waiterCount) {
        assert waiterCount >= 1;

        this.waiterCount = waiterCount;
        this.orders = new PriorityBlockingQueue<>(
            Math.max(1, programmerCount),
            Comparator.comparingInt(Programmer::getTotalEatenPortions)
        );
        this.shutdownProgrammer = new Programmer(SHUTDOWN_ORDER, programmerCount, null);
        this.activeProgrammers = new AtomicInteger(programmerCount);
        this.spoons = spoons;
    }

    public Spoon getSpoon(int id) {
        return spoons.get(id);
    }

    public void placeOrder(Programmer programmer) throws InterruptedException {
        if (!isActive()) {
            return;
        }
        orders.put(programmer);
    }

    public Programmer serveOrderBlocking() throws InterruptedException {
        return orders.take();
    }

    public void programmerFinished() {
        int remaining = activeProgrammers.decrementAndGet();
        if (remaining == 0) {
            shutdown();
        }
    }

    public void shutdown() {
        if (!active.compareAndSet(true, false)) {
            return;
        }

        for (int i = 0; i < waiterCount; i++) {
            orders.offer(shutdownProgrammer);
        }
    }

    public boolean isActive() {
        return active.get();
    }
}
