package ie.gmit.sw.helpers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Static helper class for generating random numbers.
 */
public final class Random {
    private Random() {
    }

    /**
     * Generates a random number between a given min and max.
     */
    public static int generate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Generates a random number between 0 a given max.
     */
    public static int generate(int max) {
        return generate(0, max);
    }

    /**
     * Generates a random number between 0 and the length of the given array.
     */
    public static <T> int generate(T[] arr) {
        return generate(0, arr.length - 1);
    }
}
