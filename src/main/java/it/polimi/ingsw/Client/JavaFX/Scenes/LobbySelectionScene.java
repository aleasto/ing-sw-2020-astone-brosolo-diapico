package it.polimi.ingsw.Client.JavaFX.Scenes;

import it.polimi.ingsw.Utils.ConfReader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.ParsePosition;

public class LobbySelectionScene extends SantoriniScene {

    public static final String IP_INPUT = "#ip_input";
    public static final String PORT_INPUT = "#port_input";
    public static final String CONNECT_BTN = "#connect_btn";
    public static final String LOBBY_INPUT = "#lobby_input";
    public static final String JOIN_BTN = "#join_btn";
    public static final String LOBBIES_LIST = "#lobbies_list";

    private static final String LOBBY_VIEW = "#lobby_view";

    private final Scene scene;

    public LobbySelectionScene(ConfReader confReader) {
        Label connectionDialogue = new Label("Connect to an IP");

        TextField ipInput = new TextField();
        ipInput.setPromptText("Host");
        ipInput.setText(confReader.getString("host", "localhost"));
        ipInput.setId(SET_ID(IP_INPUT));

        TextField portInput = new TextField();
        portInput.setPromptText("Port");
        portInput.setText(Integer.toString(confReader.getInt("port", 1234)));
        portInput.setId(SET_ID(PORT_INPUT));
        //Set the textfield to accept only numbers
        DecimalFormat format = new DecimalFormat("#");
        portInput.setTextFormatter(new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));

        // Top layout
        Button connectBtn = new Button("Connect");
        connectBtn.setId(SET_ID(CONNECT_BTN));
        GridPane top = new GridPane();
        VBox topVerticalLayout = new VBox(20);
        topVerticalLayout.getChildren().addAll(connectionDialogue, ipInput, portInput, connectBtn);
        top.add(topVerticalLayout, 0, 0);
        top.setPadding(new Insets(10, 10, 10, 10));

        // Bottom layout
        TextField lobbyName = new TextField();
        lobbyName.setId(SET_ID(LOBBY_INPUT));
        lobbyName.setPromptText("Lobbies");
        Label lobbiesLabel = new Label("Available lobbies: ");
        lobbiesLabel.setId(SET_ID(LOBBIES_LIST));
        Button join = new Button("Join!");
        join.setId(SET_ID(JOIN_BTN));

        HBox horizontal = new HBox(20);
        VBox bottom = new VBox(20);
        horizontal.getChildren().addAll(lobbyName, join);
        bottom.getChildren().addAll(lobbiesLabel, horizontal);
        bottom.setPadding(new Insets(10, 10, 10, 10));
        bottom.setId(SET_ID(LOBBY_VIEW));
        bottom.setVisible(false);

        // Build the layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(top);
        borderPane.setBottom(bottom);

        this.scene = new Scene(borderPane, 800, 600);
    }

    public void didConnect() {
        scene.lookup(CONNECT_BTN).setVisible(false);
        scene.lookup(LOBBY_VIEW).setVisible(true);
    }

    @Override
    public Scene getFXScene() {
        return scene;
    }
}
