package edu.wgu.dm;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;


public class RandomFactory {


    private static Random random = new Random();

    public static Long randomLong() {
        return random.nextLong();
    }

    public static Integer randomInt() {
        return random.nextInt();
    }

    public static int randomInt(int bound) {
        return random.nextInt(bound);
    }

    public static Float randomFloat() {
        return random.nextFloat();
    }

    public static String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String randomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }
}
