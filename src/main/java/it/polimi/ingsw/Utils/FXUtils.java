package it.polimi.ingsw.Utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FXUtils {
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
        if (!a.hasNext())
            a = brightColors.iterator();
        return a.next();
    }

    public static void resetColors() {
        a = brightColors.iterator();
    }

    public static void embellishLabel(Label label, Color color, int boldness) {
        label.setTextFill(color);
        label.setFont(Font.font(label.getFont().toString(), FontWeight.BOLD, boldness));
    }
}
