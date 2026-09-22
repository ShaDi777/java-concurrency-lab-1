package org.labs;

import java.util.concurrent.ThreadLocalRandom;

public class Config {
    private Config() {}

    public static int BASE_PROGRAMMER_COUNT = 7;
    public static int BASE_PORTION_COUNT = 1_000_000;
    public static int BASE_WAITER_COUNT = 2;

    public static Interval KITCHEN_WAIT_TIME_MS = new Interval(0, 5);
    public static Interval EATING_WAIT_TIME_MS = new Interval(0, 5);

    public record Interval(int first, int second) {
        public Interval {
            assert first >= 0;
            assert second >= first;
        }

        public int getBetween() {
            return ThreadLocalRandom.current().nextInt(first, second + 1);
        }
    }
}
