package it.polimi.ingsw.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
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
