package it.polimi.ingsw.Utils;

import java.util.*;

public class Utils {
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
