package it.polimi.ingsw.Client.JavaFX.Scenes;

import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.View.Communication.LobbyInfo;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

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

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1234;

    public LobbySelectionScene(ConfReader confReader) {
        Label connectionDialogue = new Label("Connect to an IP");

        TextField ipInput = new TextField();
        ipInput.setPromptText("Host");
        ipInput.setText(confReader.getString("host", DEFAULT_HOST));
        ipInput.setId(SET_ID(IP_INPUT));

        TextField portInput = new TextField();
        portInput.setPromptText("Port");
        portInput.setText(Integer.toString(confReader.getInt("port", DEFAULT_PORT)));
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
        lobbyName.setPromptText("Create or join a lobby");
        Label lobbiesLabel = new Label("Available lobbies: ");
        Button join = new Button("Join");
        join.setId(SET_ID(JOIN_BTN));

        VBox bottom = new VBox(20);
        HBox creation = new HBox(2);
        //Table View
        TableView<LobbyInfo> tableView = new TableView<>();
        TableColumn<LobbyInfo, String> column1 = new TableColumn<>("Lobby Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<LobbyInfo, String> column2 = new TableColumn<>("# Players");
        column2.setCellValueFactory(new PropertyValueFactory<>("players"));
        TableColumn<LobbyInfo, String> column3 = new TableColumn<>("# Spectators");
        column3.setCellValueFactory(new PropertyValueFactory<>("spectators"));
        TableColumn<LobbyInfo, Boolean> column4 = new TableColumn<>("Game in Progress");
        column4.setCellValueFactory(new PropertyValueFactory<>("gameRunning"));
        column4.setCellFactory(col -> new TableCell<LobbyInfo, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Yes": "No");
            }
        });

        tableView.getColumns().addAll(column1, column2, column3, column4);
        tableView.setPlaceholder(new Label("No open lobbies yet, proceed to create one"));
        tableView.setPrefHeight(300);
        tableView.setStyle("-fx-selection-bar: aquamarine; -fx-selection-bar-non-focused: lightcyan;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        column1.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        column2.setMaxWidth( 1f * Integer.MAX_VALUE * 15 ); // 15% width
        column3.setMaxWidth( 1f * Integer.MAX_VALUE * 15 ); // 15% width
        column4.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width

        tableView.setId(SET_ID(LOBBIES_LIST));

        creation.getChildren().addAll(lobbyName, join);
        bottom.getChildren().addAll(lobbiesLabel, tableView, creation);
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
