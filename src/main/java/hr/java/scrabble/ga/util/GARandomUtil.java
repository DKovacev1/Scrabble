package hr.java.scrabble.ga.util;

import hr.java.scrabble.ga.model.Direction;

import java.util.Random;

public class GARandomUtil {
    private GARandomUtil() {}

    public static final Random random = new Random();

    public static Direction getRandomDirection() {
        return Direction.values()[random.nextInt(Direction.values().length)];
    }

    public static int getRandomNumber(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
