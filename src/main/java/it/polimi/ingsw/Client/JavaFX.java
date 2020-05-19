package it.polimi.ingsw.Client;

import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.GUIColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaFX extends Application {
    private ClientRemoteView internalView;
    private Label lobbiesLabel;
    private VBox onlinePlayersLabel;
    private Button startTheGame = new Button("Start!");
    private HBox godsPlayable;
    private ArrayList<String> chosenGods = new ArrayList<>();
    private Label textMessage = new Label("");
    private boolean iAmTheHost;
    private boolean gameHasStarted = false;

    private Scene login, lobbyselection, gameplay;

    private Board board;
    private Storage storage;
    private List<MoveCommandMessage> nextMoves;
    private List<BuildCommandMessage> nextBuilds;
    private List<Player> playerList;
    private Player currentTurnPlayer;

    //Get the current screen resolution
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth();
    double height = screenSize.getHeight();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        loginScreen(primaryStage);
    }

    private void loginScreen(Stage primaryStage) {
        //Declare and initialize all our components

        //Images
        Image image = new Image("santorini.jpg");
        BackgroundFill bg = new BackgroundFill(new ImagePattern(image), CornerRadii.EMPTY, Insets.EMPTY);

        //Labels and text fields
        Label loginText = new Label("Please input your username and godlikelvl");
        loginText.setTextFill(Color.BLACK);
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

        //Buttons
        Button confirm = new Button("Enter");
        //Add the button functionality
        confirm.setOnAction(e -> {
            int level = Integer.parseInt(godlikelvlInput.getText());
            Player player = new Player(nameInput.getText(), level);
            //Switch to lobby screen on press
            lobbyScreen(primaryStage, player);
        });

        //Layout declaration
        GridPane general = new GridPane();
        VBox layout = new VBox(20);
        HBox setlayout = new HBox(20);

        //Add components to the layouts and set the scene
        setlayout.getChildren().addAll(nameInput, godlikelvlInput);
        layout.getChildren().addAll(loginText, setlayout, confirm);
        general.setAlignment(Pos.CENTER);
        general.add(layout, 0, 0);
        general.setBackground(new Background(bg));
        //Initialize the scene
        login = new Scene(general, 800, 600);

        //Set the stage
        primaryStage.setTitle("Login to Santorini");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(login);
        primaryStage.show();
    }

    private void lobbyScreen(Stage primaryStage, Player player) {
        //Declare and initialize all our components

        //Labels and text fields
        Label text = new Label("Welcome " + player.getName() + " with a God power of " + player.getGodLikeLevel());

        Label connectionDialogue = new Label("Connect to an IP");

        TextField ipInput = new TextField();
        ipInput.setText("localhost");
        ipInput.setPromptText("IP");

        //Buttons
        Button confirmation = new Button("Connect");

        //Declare the layout
        BorderPane global = new BorderPane();
        GridPane general = new GridPane();
        VBox layout = new VBox(20);

        //Declare our RemoteView
        internalView = new ClientRemoteView(player) {
            @Override
            public void onDisconnect() {
                reset("Connection dropped.");
            }

            @Override
            public void onBoardUpdate(BoardUpdateMessage message) {
                if (!gameHasStarted) {
                    gameHasStarted = true;
                    godpoolDisplay(primaryStage);
                }
                board = message.getBoard();
            }

            @Override
            public void onLobbiesUpdate(LobbiesUpdateMessage message) {
                Platform.runLater(() -> {
                    if (lobbiesLabel == null) {
                        lobbiesLabel = new Label();
                    }
                    lobbiesLabel.setText("Available lobbies: " + String.join(", ", message.getLobbyNames()));
                });
            }

            @Override
            public void onNextActionsUpdate(NextActionsUpdateMessage message) {

            }

            @Override
            public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {

            }

            @Override
            public void onPlayersUpdate(PlayersUpdateMessage message) {
                Platform.runLater(() -> {
                    if(onlinePlayersLabel == null) {
                        onlinePlayersLabel = new VBox(10);
                    }
                    onlinePlayersLabel.getChildren().clear();

                    iAmTheHost = message.getPlayerList().get(0).equals(player);
                    if(!iAmTheHost) {
                        startTheGame.setVisible(false);
                    }

                    List<String> playerList = message.getPlayerList().stream().map(Player::getName).collect(Collectors.toList());
                    for(String p : playerList) {
                        Label label = new Label();
                        label.setText(p);
                        label.setTextFill(GUIColor.uniqueColor());
                        label.setFont(Font.font(label.getFont().toString(), FontWeight.BOLD, 15));
                        onlinePlayersLabel.getChildren().add(label);
                    }
                });
            }

            @Override
            public void onShowGods(GodListMessage message) {
                Platform.runLater(() -> {
                    if(godsPlayable == null) {
                        godsPlayable = new HBox(20);
                    }
                    godsPlayable.getChildren().clear();
                    List<String> gods = message.getGods();
                    for (String god : gods) {
                        //Create the god image
                        ImageView imageView = new ImageView(new Image("/godcards/" + god + ".png", width / 15, height * 0.4, true, true));
                        //Set click functionality on the images
                        imageView.setPickOnBounds(true);
                        imageView.setOnMouseClicked((MouseEvent e) -> {
                            if (e.getButton() == MouseButton.PRIMARY) {
                                //Left click to select
                                Glow glow = new Glow();
                                imageView.setEffect(glow);
                                chosenGods.add(god);
                            } else if (e.getButton() == MouseButton.SECONDARY) {
                                //Right click to deselect
                                imageView.setEffect(null);
                                chosenGods.remove(god);
                            }
                        });
                        godsPlayable.getChildren().add(imageView);
                    }
                });
            }

            @Override
            public void onStorageUpdate(StorageUpdateMessage message) {

            }

            @Override
            public void onText(TextMessage message) {
                Platform.runLater(() -> {
                    textMessage.setText(message.getText());
                });
            }
        };

        //Add button functionality
        confirmation.setOnAction(e -> {
            boolean ipIsCorrect;
            try {
                internalView.connect(ipInput.getText());
                internalView.startNetworkThread();
                ipIsCorrect = true;
            } catch (IOException ex) {
                ipIsCorrect = false;
                //Alert Box configuration
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid IP");
                alert.showAndWait();
            }
            if(ipIsCorrect) {
                //Declare components
                TextField lobbyName = new TextField();
                lobbyName.setPromptText("Lobbies");
                Button join = new Button("Join!");

                //Declare internal layout
                HBox lobbySetup = new HBox(20);
                VBox lobbyLayout = new VBox(20);
                lobbySetup.getChildren().addAll(lobbyName, join);
                if (lobbiesLabel == null) {
                    lobbiesLabel = new Label("Available lobbies: ");
                }
                lobbyLayout.getChildren().addAll(lobbiesLabel, lobbySetup);
                lobbyLayout.setPadding(new Insets(10, 10, 10, 10));
                global.setBottom(lobbyLayout);
                confirmation.setVisible(false);

                //Add button functionality
                join.setOnAction(x -> {
                    //Switch to gameplay screen on press and connect
                    if (!lobbyName.getText().trim().isEmpty()) {
                        internalView.join(lobbyName.getText());
                        primaryStage.setMaximized(true);
                        pregame(primaryStage);
                    } else {
                        //Alert Box configuration
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Lobby Name can't be empty");
                        alert.showAndWait();
                    }
                });
            }
        });

        //Add components to the external layout
        layout.getChildren().addAll(text, connectionDialogue, ipInput, confirmation);
        general.add(layout, 0, 0);
        general.setPadding(new Insets(10, 10, 10, 10));
        global.setTop(general);
        //Initialize the scene
        lobbyselection = new Scene(global, 800, 600);

        //Set the stage
        primaryStage.setTitle("Santorini Lobby Selection Screen");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(lobbyselection);
        primaryStage.show();
    }

    private void pregame(Stage primaryStage) {
        //Declare the components
        Image imgCenter = new Image("SantoriniBoard.png", width, height, true, true);
        Rectangle transparency = new Rectangle(width, height);
        transparency.setFill(Color.rgb(0, 0, 0, 0.90));
        Rectangle onlinePlayersBG = new Rectangle(250, height / 2, Color.ORANGE);

        //Declare the layouts
        if (onlinePlayersLabel == null) {
            onlinePlayersLabel = new VBox(10);
        }
        onlinePlayersLabel.setAlignment(Pos.CENTER);

        AnchorPane anchorPane = new AnchorPane(onlinePlayersBG, onlinePlayersLabel);
        StackPane stack = new StackPane(new ImageView(imgCenter), transparency, anchorPane);

        //Anchor the components
        AnchorPane.setTopAnchor(onlinePlayersLabel, 15d);
        AnchorPane.setRightAnchor(onlinePlayersLabel, 120d);
        AnchorPane.setTopAnchor(onlinePlayersBG, 1d);
        AnchorPane.setRightAnchor(onlinePlayersBG, 1d);

        //Handle the Start Game Button
        anchorPane.getChildren().add(startTheGame);
        AnchorPane.setBottomAnchor(startTheGame, (height / 2) - 50);
        AnchorPane.setRightAnchor(startTheGame, (width / 2) - 20);
        //Add button functionality
        startTheGame.setOnAction(e -> {
            internalView.onCommand(new StartGameCommandMessage());
            godpoolDisplay(primaryStage);
           });

        //Initialize the scene
        gameplay = new Scene(stack, width, height);

        //Set the stage
        primaryStage.setTitle("Santorini");
        primaryStage.setFullScreen(true);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.setMinWidth(width);
        primaryStage.setMinHeight(height);
        primaryStage.setScene(gameplay);
        primaryStage.show();
    }

    private void godpoolDisplay(Stage primaryStage) {
        Stage window = new Stage();

        window.setX(0);
        window.setY(height / 5);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMaxWidth(width);

        Button b = new Button("Select");
        VBox vBox = new VBox(2);

        b.setOnAction(e -> {
            for (String god : chosenGods) {
                System.out.println(god);
            }
            window.close();
            gameplayScreen(primaryStage);
        });

        if(godsPlayable == null) {
            godsPlayable = new HBox(20);
        }
        godsPlayable.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(godsPlayable, b);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(45);
        Scene scene = new Scene(vBox, width, height * 0.66);
        window.setScene(scene);
        window.showAndWait();
    }

    private void gameplayScreen(Stage primaryStage) {

        //Declare the components
        Image imgCenter = new Image("SantoriniBoard.png", width, height, true, true);
        Rectangle r1 = new Rectangle(350, 500);
        r1.setFill(Color.ORANGE);

        //Declare the layouts
        GridPane grid = new GridPane();
        AnchorPane anchorPane = new AnchorPane(r1);
        AnchorPane.setRightAnchor(r1, 1d);
        StackPane stack = new StackPane(new ImageView(imgCenter), anchorPane, grid);


        //Add components to the layout
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(15, 0, 0, 0));
        grid.setHgap(13);
        grid.setVgap(13);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Rectangle rectangle = new Rectangle(120, 120);
                rectangle.setFill(Color.TRANSPARENT);
                GridPane.setRowIndex(rectangle, i);
                GridPane.setColumnIndex(rectangle, j);
                grid.getChildren().add(rectangle);
            }
        }

        //Add a mouse press event to the gridPane
        grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                for (Node node : grid.getChildren()) {
                    if (node instanceof Rectangle) {
                        if (node.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                            System.out.println("Node: " + node + " at " + GridPane.getRowIndex(node) + "/" + GridPane.getColumnIndex(node));
                        }
                    }
                }
            }
        });

        //Initialize the scene
        gameplay = new Scene(stack, width, height);

        //Set the stage
        primaryStage.setFullScreen(true);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.setMinWidth(width);
        primaryStage.setMinHeight(height);
        primaryStage.setScene(gameplay);
        primaryStage.show();
    }

    public void reset(String msg) {
        board = null;
        storage = null;
        nextMoves = null;
        playerList = null;
        currentTurnPlayer = null;

        internalView.onText(new TextMessage(msg));
    }
}
