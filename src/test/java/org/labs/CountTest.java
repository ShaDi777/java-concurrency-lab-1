package org.labs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.labs.equal.EqualRestaurant;

public abstract class CountTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("provideIntNumbers")
    public void multipleProgrammers(int programmerCount) {
        for (int i = 0; i < REPETITION_COUNT; i++) {
            var restaurant = createRestaurant(programmerCount, PORTION_COUNT, 4);
            restaurant.simulateBlocking(50);

            var actualResult = restaurant.getProgrammerResult().values().stream().mapToInt(e -> e).sum();
            Assertions.assertEquals(PORTION_COUNT, actualResult);
        }
    }
}

class SimpleCountTest extends CountTest {
    @Override
    protected Restaurant createRestaurant(int programmers, int portions, int waiters) {
        return new Restaurant(programmers, portions, waiters);
    }
}

class EqualCountTest extends CountTest {
    @Override
    protected Restaurant createRestaurant(int programmers, int portions, int waiters) {
        return new EqualRestaurant(programmers, portions, waiters);
    }
}
