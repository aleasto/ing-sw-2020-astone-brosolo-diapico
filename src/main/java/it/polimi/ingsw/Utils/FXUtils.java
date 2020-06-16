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

    /**
     * Get a (somewhat) unique ANSI color.
     * Will always generate the same sequence of colors.
     * It cannot be truly unique since we track a limited number of colors.
     * Can be reset to the start with resetColors()
     *
     * @return a function to apply an ANSI color to a string
     */
    public static Color uniqueColor() {
        if (!a.hasNext())
            a = brightColors.iterator();
        return a.next();
    }

    /**
     * Reset the colors so that uniqueColor() starts from the beginning
     */
    public static void resetColors() {
        a = brightColors.iterator();
    }

    /**
     * Style a label
     * @param label the label
     * @param color the color
     * @param boldness the font size
     */
    public static void embellishLabel(Label label, Color color, int boldness) {
        label.setTextFill(color);
        label.setFont(Font.font(label.getFont().toString(), FontWeight.BOLD, boldness));
    }
}
