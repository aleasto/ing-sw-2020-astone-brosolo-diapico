package it.polimi.ingsw.Utils;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EmbellishLabel {
    public static void embellishLabel(Label label, Color color, int boldness) {
        label.setTextFill(color);
        label.setFont(Font.font(label.getFont().toString(), FontWeight.BOLD, boldness));
    }
}
