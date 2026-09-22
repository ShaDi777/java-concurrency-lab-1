package org.labs;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.labs.equal.EqualRestaurant;

public abstract class CompletionTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("provideIntNumbers")
    public void multipleProgrammers(int programmerCount) {
        for (int i = 0; i < REPETITION_COUNT; i++) {
            var restaurant = createRestaurant(programmerCount, PORTION_COUNT, 1);
            restaurant.simulateBlocking(5);
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntNumbers")
    public void multipleWaitersFinishInAcceptableTime(int waiterCount) {
        for (int i = 0; i < REPETITION_COUNT; i++) {
            var restaurant = new Restaurant(2, PORTION_COUNT, waiterCount);
            restaurant.simulateBlocking(5);
        }
    }
}

class SimpleCompletionTest extends CompletionTest {
    @Override
    protected Restaurant createRestaurant(int programmers, int portions, int waiters) {
        return new Restaurant(programmers, portions, waiters);
    }
}

class EqualCompletionTest extends CompletionTest {
    @Override
    protected Restaurant createRestaurant(int programmers, int portions, int waiters) {
        return new EqualRestaurant(programmers, portions, waiters);
    }
}
