package org.labs;

import java.util.stream.Stream;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Timeout(value = 60)
@Execution(ExecutionMode.CONCURRENT)
public abstract class BaseTest {
    protected static final int REPETITION_COUNT = 3;
    protected static final int PORTION_COUNT = 1000;
    protected static final Integer[] INT_ARRAY = {2, 3, 7, 50, 1001};

    public static Stream<Integer> provideIntNumbers() {
        return Stream.of(INT_ARRAY);
    }

    protected abstract Restaurant createRestaurant(int programmers, int portions, int waiters);
}
