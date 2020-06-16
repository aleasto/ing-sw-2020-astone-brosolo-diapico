package it.polimi.ingsw.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ConfReader {
    private final Map<String, String> mapRep = new HashMap<>();
    private final String source;

    /**
     * Create a configuration file reader.
     * If the file does not exist, try to generate a default for it
     * @param filename the configuration file
     * @throws IOException if we fail to generate the default file, or if no default is known
     */
    public ConfReader(String filename) throws IOException {
        this.source = filename;
        File file = new File(source);
        if (!file.exists()) {
            System.out.println(source + " file not found. Generating defaults.");
            try {
                Files.copy(getClass().getResourceAsStream("/" + filename), file.toPath());
            } catch (NullPointerException ex) {
                throw new IOException("No defaults file found for `" + source + "`");
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
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

    /**
     * Parse a string config entry
     * @param key the key
     * @param defaultVal the fallback value to return if key was not in the file
     * @return the value for key, or fallback
     */
    public String getString(String key, String defaultVal) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVal;
        }
        return val;
    }

    /**
     * Parse an integer config entry
     * @param key the key
     * @param defaultVal the fallback value to return if key was not in the file
     * @return the value for key, or fallback
     */
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

    /**
     * Parse a boolean config entry
     * @param key the key
     * @param defaultVal the fallback value to return if key was not in the file
     * @return the value for key, or fallback
     */
    public boolean getBoolean(String key, boolean defaultVal) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVal;
        }
        return Boolean.parseBoolean(val);   // returns false for anything else than "true"
    }

    /**
     * Parse an integer array config entry
     * @param key the key
     * @param defaultVals the fallback values to return if key was not in the file
     * @return the value for key, or fallback
     */
    public Integer[] getInts(String key, Integer ...defaultVals) {
        String val = mapRep.get(key);
        if (val == null) {
            System.out.println("No `" + key + "` config in " + source);
            return defaultVals;
        }

        return Utils.parseIntsSafe(val);
    }

    /**
     * Parse an integer pair entry
     *
     * @param key the key
     * @param defaultFirst the fallback value for the first entry of the pair if key was not in the file
     * @param defaultSecond the fallback value for the first entry of the pair if key was not in the file
     * @return the value for key, or fallback
     */
    public Pair<Integer, Integer> getIntPair(String key, Integer defaultFirst, Integer defaultSecond) {
        Integer[] arr = getInts(key, defaultFirst, defaultSecond);
        if (arr.length != 2) {
            System.out.println("Malformed Pair<Integer, Integer> for `" + key + "` in " + source);
            return new Pair<>(defaultFirst, defaultSecond);
        }
        return new Pair<>(arr[0], arr[1]);
    }
}
