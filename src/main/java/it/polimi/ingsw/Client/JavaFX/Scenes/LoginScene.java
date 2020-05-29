package it.polimi.ingsw.Client.JavaFX.Scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.DecimalFormat;
import java.text.ParsePosition;

public class LoginScene extends SantoriniScene {

    public static final String NAME_INPUT = "#name_input";
    public static final String LVL_INPUT = "#lvl_input";
    public static final String LOGIN_BTN = "#login_btn";

    private Scene scene;

    public LoginScene() {
        Image image = new Image("santorini.jpg");
        BackgroundFill bg = new BackgroundFill(new ImagePattern(image), CornerRadii.EMPTY, Insets.EMPTY);

        //Labels and text fields
        Label loginPrompt = new Label("Please input your username and godlikelvl");
        loginPrompt.setTextFill(Color.BLACK);
        loginPrompt.setFont(Font.font(loginPrompt.getFont().toString(), FontWeight.BOLD, 15));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Username");
        nameInput.setId(SET_ID(NAME_INPUT));

        TextField lvlInput = new TextField();
        lvlInput.setPromptText("Godlike level");
        lvlInput.setId(SET_ID(LVL_INPUT));

        //Set the textfield to accept only numbers
        DecimalFormat format = new DecimalFormat("#");
        lvlInput.setTextFormatter(new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));

        Button enterBtn = new Button("Enter");
        enterBtn.setId(SET_ID(LOGIN_BTN));

        //Layout declaration
        GridPane grid = new GridPane();
        VBox verticalLayout = new VBox(20);
        HBox inputBox = new HBox(20);

        //Add components to the layouts and set the scene
        inputBox.getChildren().addAll(nameInput, lvlInput);
        verticalLayout.getChildren().addAll(loginPrompt, inputBox, enterBtn);
        grid.setAlignment(Pos.CENTER);
        grid.add(verticalLayout, 0, 0);
        grid.setBackground(new Background(bg));

        this.scene = new Scene(grid, 800, 600);
    }

    @Override
    public Scene getFXScene() {
        return scene;
    }
}
