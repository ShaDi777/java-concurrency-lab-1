package org.labs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.labs.equal.EqualRestaurant;

public class EqualFairnessTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("provideIntNumbers")
    public void multipleProgrammers(int programmerCount) {
        for (int i = 0; i < REPETITION_COUNT; i++) {
            var restaurant = createRestaurant(programmerCount, PORTION_COUNT, 4);
            restaurant.simulateBlocking(50);

            var fairResult = PORTION_COUNT / programmerCount;
            var actualResults = restaurant.getProgrammerResult().values();

            Assertions.assertTrue(actualResults.stream().allMatch(
                actualResult -> Math.abs(actualResult - fairResult) <= 1
            ));
        }
    }

    @Override
    protected Restaurant createRestaurant(int programmers, int portions, int waiters) {
        return new EqualRestaurant(programmers, portions, waiters);
    }
}