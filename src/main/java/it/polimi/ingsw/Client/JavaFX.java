package it.polimi.ingsw.Client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParsePosition;

public class JavaFX extends Application {

    Scene login, lobbyselection, gameplay;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login to Santorini");

        //Declare and initialize all our components
        Image image = new Image("santorini.jpg");
        BackgroundFill bg = new BackgroundFill(new ImagePattern(image), CornerRadii.EMPTY, Insets.EMPTY);

        Label loginText = new Label("Please input your username and godlikelvl");
        loginText.setTextFill(Color.rgb(0, 0, 0));
        loginText.setFont(Font.font(loginText.getFont().toString(), FontWeight.BOLD, 15));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Username");

        TextField godlikelvlInput = new TextField();
        godlikelvlInput.setPromptText("Godlikelvl");
        //Set the textfield to accept only numbers
        DecimalFormat format = new DecimalFormat("#");
        godlikelvlInput.setTextFormatter(new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));

        Button confirm = new Button("Connect");


        //Add the button functionality
        confirm.setOnAction(e -> {
            int level = Integer.parseInt(godlikelvlInput.getText());
            lobbyScreen(primaryStage, nameInput.getText(), level);
        });

        //Build the layout, add all components to it and set the scene
        GridPane general = new GridPane();
        VBox layout = new VBox(20);
        HBox setlayout = new HBox(20);
        setlayout.getChildren().addAll(nameInput, godlikelvlInput);
        layout.getChildren().addAll(loginText, setlayout, confirm);
        general.setAlignment(Pos.CENTER);
        general.add(layout, 0, 0);
        general.setBackground(new Background(bg));
        login = new Scene(general, 800, 600);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(login);
        primaryStage.show();
    }

    private void lobbyScreen(Stage primaryStage, String name, int godlikelvl) {
        primaryStage.setTitle("Santorini Lobby Selection Screen");

        Label text = new Label("Welcome " + name + " with a God power of " + godlikelvl);
        Label connectionDialogue = new Label("Connect to an IP");
        TextField ipInput = new TextField();
        ipInput.setPromptText("IP");
        Button confirmation = new Button("Connect");

        BorderPane global = new BorderPane();
        GridPane general = new GridPane();
        VBox layout = new VBox(20);

        confirmation.setOnAction( e -> {
            // Show lobbies
            Label temp = new Label("Available lobbies:");

            TextField lobbyName = new TextField();
            lobbyName.setPromptText("Lobby name");
            Button join = new Button("Join!");

            HBox lobbySetup = new HBox(20);
            VBox lobbyLayout = new VBox(20);
            lobbySetup.getChildren().addAll(lobbyName, join);
            // Add lobbies
            lobbyLayout.getChildren().addAll(temp, lobbySetup);
            lobbyLayout.setPadding(new Insets(10, 10, 10, 10));
            global.setBottom(lobbyLayout);

            join.setOnAction( x -> {
                primaryStage.setMaximized(true);
                gameplayScreen(primaryStage, name, godlikelvl, ipInput.getText(), lobbyName.getText());
            });
        });

        layout.getChildren().addAll(text, connectionDialogue, ipInput, confirmation);
        general.add(layout, 0, 0);
        general.setPadding(new Insets(10, 10, 10, 10));
        global.setTop(general);
        lobbyselection = new Scene(global, 800, 600);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(lobbyselection);
        primaryStage.show();
    }

    private void gameplayScreen(Stage primaryStage, String name, int godlikelvl, String ip, String lobby) {
        primaryStage.setTitle("Santorini");

        Image imgCenter = new Image("SantoriniGrid.png", 0, 0, true, true);
        BackgroundFill bg = new BackgroundFill(new ImagePattern(imgCenter), CornerRadii.EMPTY, Insets.EMPTY);
        ImageView img = new ImageView(imgCenter);
        img.fitWidthProperty().bind(primaryStage.widthProperty());
        GridPane general = new GridPane();
        general.setAlignment(Pos.CENTER);
        general.setBackground(new Background(bg));
        BorderPane global = new BorderPane();
        global.setCenter(general);

        gameplay = new Scene(global, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setMinHeight(primaryScreenBounds.getMinX());
        primaryStage.setMinWidth(primaryScreenBounds.getMinY());
        primaryStage.setScene(gameplay);
        primaryStage.show();
    }
}
