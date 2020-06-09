package it.polimi.ingsw.Utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javafx.scene.paint.Color;

public class GUIColor {
    private static final List<Color> brightColors = Arrays.asList(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.MAGENTA,
            Color.YELLOW,
            Color.CYAN
    );

    static Iterator<Color> a = brightColors.iterator();

    public static Color uniqueColor() {
        return a.next();
    }

    public static void reset() {
        a = brightColors.iterator();
    }
}
