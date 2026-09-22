package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static org.labs.Config.BASE_PORTION_COUNT;
import static org.labs.Config.BASE_PROGRAMMER_COUNT;
import static org.labs.Config.BASE_WAITER_COUNT;

public class Restaurant {

    private final Table table;
    private final List<Programmer> programmers;
    private final List<Waiter> waiters;
    private final ExecutorService executor;

    public Restaurant() {
        this(BASE_PROGRAMMER_COUNT, BASE_PORTION_COUNT, BASE_WAITER_COUNT);
    }

    public Restaurant(int programmerCount, int portionCount, int waiterCount) {
        Kitchen kitchen = new Kitchen(portionCount);

        List<Spoon> spoons = new ArrayList<>(programmerCount);
        for (int i = 0; i < programmerCount; i++) {
            spoons.add(new Spoon(i));
        }

        this.table = new Table(spoons, programmerCount, waiterCount);

        this.programmers = new ArrayList<>(programmerCount);
        for (int i = 0; i < programmerCount; i++) {
            programmers.add(new Programmer(i, programmerCount, this.table));
        }

        this.waiters = new ArrayList<>(waiterCount);
        for (int i = 0; i < waiterCount; i++) {
            this.waiters.add(new Waiter(i, this.table, kitchen));
        }

        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        // this.executor = Executors.newFixedThreadPool(programmerCount + waiterCount)
    }

    protected Restaurant(Table table, List<Programmer> programmers, List<Waiter> waiters, ExecutorService executor) {
        this.table = table;
        this.programmers = programmers;
        this.waiters = waiters;
        this.executor = executor;
    }

    // Returns true if finished in time
    public void simulateBlocking(int timeoutSeconds) {
        try {
            waiters.forEach(executor::submit);
            programmers.forEach(executor::submit);

            executor.shutdown();
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                table.shutdown();
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            table.shutdown();
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Stats
    public Map<String, Integer> getProgrammerResult() {
        return programmers.parallelStream().collect(Collectors.toMap(
            programmer -> "programmer-" + programmer.getId(),
            Programmer::getTotalEatenPortions
        ));
    }

    public Map<String, Integer> getWaiterResult() {
        return waiters.parallelStream().collect(Collectors.toMap(
            waiter -> "waiter-" + waiter.getId(),
            Waiter::getServedOrders
        ));
    }
}
