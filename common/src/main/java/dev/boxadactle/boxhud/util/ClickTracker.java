package dev.boxadactle.boxhud.util;

import java.util.LinkedList;

public class ClickTracker {

    private static final LinkedList<Long> leftClickTimestamps = new LinkedList<>();
    private static final LinkedList<Long> rightClickTimestamps = new LinkedList<>();
    private static final long TIME_WINDOW_MS = 1000;

    public static void clickLeft() {
        long currentTime = System.currentTimeMillis();
        leftClickTimestamps.add(currentTime);
        removeOldTimestamps(leftClickTimestamps, currentTime);
    }

    public static void clickRight() {
        long currentTime = System.currentTimeMillis();
        rightClickTimestamps.add(currentTime);
        removeOldTimestamps(rightClickTimestamps, currentTime);
    }

    public static int getLeftCPS() {
        long currentTime = System.currentTimeMillis();
        removeOldTimestamps(leftClickTimestamps, currentTime);
        return leftClickTimestamps.size();
    }

    public static int getRightCPS() {
        long currentTime = System.currentTimeMillis();
        removeOldTimestamps(rightClickTimestamps, currentTime);
        return rightClickTimestamps.size();
    }

    private static void removeOldTimestamps(LinkedList<Long> timestamps, long currentTime) {
        while (!timestamps.isEmpty() && currentTime - timestamps.getFirst() > TIME_WINDOW_MS) {
            timestamps.removeFirst();
        }
    }
}
