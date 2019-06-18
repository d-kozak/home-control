package io.dkozak.house.control.client;

public class Utils {

    public static int requireNonNegative(int x) {
        if (x < 0) {
            throw new IllegalArgumentException(x + " is negative");
        }
        return x;
    }
}
