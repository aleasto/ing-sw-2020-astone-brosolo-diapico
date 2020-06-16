package it.polimi.ingsw.Utils;

import java.util.*;

public class Utils {
    /**
     * A shorthand for creating a timer
     *
     * @param runnable the method to be called every period
     * @param period the time period. The first tick will happen after one period
     * @return a new Timer
     */
    public static Timer makeTimer(MyTimerRunnable runnable, int period) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.onTick();
            }
        }, period, period);
        return timer;
    }

    /**
     * Safely parse a string representing an array of integers, like [1, 2, 3]
     *
     * @param str the input string
     * @return the parsed array
     */
    public static Integer[] parseIntsSafe(String str) {
        str = str.replaceAll("[\\[\\]]", ""); // Ignore [] brackets
        Scanner scanner = new Scanner(str);
        scanner.useDelimiter("[,\\s]+");
        List<Integer> out = new ArrayList<>();
        while (scanner.hasNextInt()) {
            out.add(scanner.nextInt());
        }
        Integer[] outArray = new Integer[out.size()];
        out.toArray(outArray);
        return outArray;
    }
}
