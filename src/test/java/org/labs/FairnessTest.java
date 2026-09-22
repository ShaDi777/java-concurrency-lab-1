package org.labs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Execution(ExecutionMode.SAME_THREAD)
public class FairnessTest extends BaseTest {
    public static int REPETITION_COUNT = 1;

    @ParameterizedTest
    @ValueSource(ints =  {2, 3, 7, 50})
    public void multipleProgrammers(int programmerCount) {
        for (int i = 0; i < REPETITION_COUNT; i++) {
            var restaurant = createRestaurant(programmerCount, PORTION_COUNT, 4);
            restaurant.simulateBlocking(50);

            var fairResult = PORTION_COUNT / programmerCount;
            var actualResults = restaurant.getProgrammerResult().values();

            System.out.println(actualResults);

            var maxResult = actualResults.stream().max(Integer::compareTo).get();
            var minResult = actualResults.stream().min(Integer::compareTo).get();
            var actualDifference = maxResult - minResult;

            Assertions.assertTrue(actualDifference < fairResult);
        }
    }

    @Override
    protected Restaurant createRestaurant(int programmers, int portions, int waiters) {
        return new Restaurant(programmers, portions, waiters);
    }
}
