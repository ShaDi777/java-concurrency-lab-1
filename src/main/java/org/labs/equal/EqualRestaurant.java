package org.labs.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.labs.Kitchen;
import org.labs.Programmer;
import org.labs.Restaurant;
import org.labs.Spoon;
import org.labs.Table;
import org.labs.Waiter;
import static org.labs.Config.BASE_PORTION_COUNT;
import static org.labs.Config.BASE_PROGRAMMER_COUNT;
import static org.labs.Config.BASE_WAITER_COUNT;

public class EqualRestaurant extends Restaurant {

    public EqualRestaurant() {
        this(BASE_PROGRAMMER_COUNT, BASE_PORTION_COUNT, BASE_WAITER_COUNT);
    }

    public EqualRestaurant(int programmerCount, int portionCount, int waiterCount) {
        this(buildParts(programmerCount, portionCount, waiterCount));
    }

    private EqualRestaurant(Parts p) {
        super(p.table, p.programmers, p.waiters, p.executor);
    }

    private static Parts buildParts(int programmerCount, int portionCount, int waiterCount) {
        Kitchen kitchen = new EqualKitchen(portionCount, programmerCount);

        List<Spoon> spoons = new ArrayList<>(programmerCount);
        for (int i = 0; i < programmerCount; i++) {
            spoons.add(new EqualSpoon(i));
        }

        Table table = new Table(spoons, programmerCount, waiterCount);

        List<Programmer> programmers = new ArrayList<>(programmerCount);
        for (int i = 0; i < programmerCount; i++) {
            programmers.add(new Programmer(i, programmerCount, table));
        }

        List<Waiter> waiters = new ArrayList<>(waiterCount);
        for (int i = 0; i < waiterCount; i++) {
            waiters.add(new Waiter(i, table, kitchen));
        }

        var executor = Executors.newVirtualThreadPerTaskExecutor();

        return new Parts(table, programmers, waiters, executor);
    }

    private record Parts(
        Table table,
        List<Programmer> programmers,
        List<Waiter> waiters,
        ExecutorService executor
    ) {
    }
}
