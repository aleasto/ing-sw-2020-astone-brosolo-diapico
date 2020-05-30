package it.polimi.ingsw.Utils;

import java.io.*;
import java.util.*;

public class ConfReader {
    private final Map<String, String> mapRep = new HashMap<>();
    private final String source;

    public ConfReader(String filename) throws IOException {
        this.source = filename;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        while (line != null) {
            Scanner scanner = new Scanner(line);
            scanner.useDelimiter(": ");
            try {
                mapRep.put(scanner.next(), scanner.next());
            } catch (NoSuchElementException ex) {
                System.out.println(source + ": skipping malformed line `" + line + "`");
            }
            line = reader.readLine();
        }
    }

    public String getString(String key, String defaultVal) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVal;
        }
        return val;
    }

    public int getInt(String key, int defaultVal) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVal;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            System.out.println("Malformed integer for `" + key + "` in " + source);
            return defaultVal;
        }
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVal;
        }
        return Boolean.parseBoolean(val);   // returns false for anything else than "true"
    }

    public Integer[] getInts(String key, Integer ...defaultVals) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVals;
        }

        return Utils.parseIntsSafe(val);
    }

    public Pair<Integer, Integer> getIntPair(String key, Integer defaultFirst, Integer defaultSecond) {
        Integer[] arr = getInts(key, defaultFirst, defaultSecond);
        if (arr.length != 2) {
            System.out.println("Malformed Pair<Integer, Integer> for `" + key + "` in " + source);
            return new Pair<>(defaultFirst, defaultSecond);
        }
        return new Pair<>(arr[0], arr[1]);
    }
}
