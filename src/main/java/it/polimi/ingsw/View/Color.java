package it.polimi.ingsw.View;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class Color {
    private static final String _black =   "\u001b[30m";
    private static final String _red =     "\u001b[31m";
    private static final String _green =   "\u001b[32m";
    private static final String _yellow =  "\u001b[33m";
    private static final String _blue =    "\u001b[34m";
    private static final String _magenta = "\u001b[35m";
    private static final String _cyan =    "\u001b[36m";
    private static final String _white =   "\u001b[37m";
    private static final String _reset =   "\u001b[0m";

    public static String BLACK(String s) {
        return _black + s + _reset;
    }

    public static String RED(String s) {
        return _red + s + _reset;
    }

    public static String GREEN(String s) {
        return _green + s + _reset;
    }

    public static String YELLOW(String s) {
        return _yellow + s + _reset;
    }

    public static String BLUE(String s) {
        return _blue + s + _reset;
    }

    public static String MAGENTA(String s) {
        return _magenta + s + _reset;
    }

    public static String CYAN(String s) {
        return _cyan + s + _reset;
    }

    public static String WHITE(String s) {
        return _white + s + _reset;
    }

    public static String NONE(String s) {
        return s;
    }

    private static final List<Function<String, String>> brightColors = Arrays.asList(
            Color::RED,
            Color::GREEN,
            Color::BLUE,
            Color::MAGENTA,
            Color::YELLOW,
            Color::CYAN
    );

    static Iterator<Function<String, String>> a = brightColors.iterator();
    public static Function<String, String> uniqueColor() {
        return a.next();
    }
}
