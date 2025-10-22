package dev.boxadactle.boxhud.util;

import java.util.Collections;
import java.util.LinkedList;

public class FpsTracker {

    public static int maxHistory = 250;

    private static final LinkedList<Integer> history = new LinkedList<>();

    public static int getMinimum() {
        return Collections.min(history);
    }

    public int getMaximum() {
        return Collections.max(history);
    }

    public static int getAverage() {
        int sum = 0;
        for (int fps : history) {
            sum += fps;
        }
        return sum / history.size();
    }

    public static int getMs() {
        int averageFps = getAverage();
        if (averageFps == 0) return 0;
        return 1000 / averageFps;
    }

    // uses standard deviation of frame time (1000/fps) to calculate a lag score
    public static int getLagScore() {
        synchronized (history) {
            if (history.isEmpty()) return 0;

            int count = 0;
            double sumMs = 0.0;
            for (int fps : history) {
                if (fps <= 0) continue;
                sumMs += 1000.0 / fps;
                count++;
            }
            if (count == 0) return 0;

            double meanMs = sumMs / count;
            double varSum = 0.0;
            for (int fps : history) {
                if (fps <= 0) continue;
                double ms = 1000.0 / fps;
                double diff = ms - meanMs;
                varSum += diff * diff;
            }

            double variance = varSum / count;
            double stdev = Math.sqrt(variance);

            if (meanMs == 0) return 0;
            // normalize stdev by mean and scale to percentage
            return (int) ((stdev / meanMs) * 100.0);
        }
    }

    public static void recent(int fps) {
        synchronized (history) {
            history.addLast(fps);
            while (history.size() > maxHistory) {
                history.removeFirst();
            }
        }
    }
}
