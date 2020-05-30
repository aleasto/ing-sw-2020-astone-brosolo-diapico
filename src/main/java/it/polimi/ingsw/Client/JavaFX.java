package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.Scenes.GameplayScene;
import it.polimi.ingsw.Client.Scenes.LobbySelectionScene;
import it.polimi.ingsw.Client.Scenes.LoginScene;
import it.polimi.ingsw.Client.Scenes.SantoriniScene;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.GUIColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFX extends Application {
    private ClientRemoteView internalView;
    private Label textMessage = new Label("");
    private boolean iAmTheHost;
    private boolean gameHasStarted = false;
    private Player currentTurn;

    private Stage mainStage;
    private LoginScene loginScene;
    private LobbySelectionScene lobbySelectionScene;
    private GameplayScene gameplayScene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);

        // Create the three scenes
        loginScene = new LoginScene();
        lobbySelectionScene = new LobbySelectionScene();
        gameplayScene = new GameplayScene();

        // Hook up the clickables
        loginScene.<Button>lookup(LoginScene.LOGIN_BTN).setOnAction(e -> {
            String playerName = loginScene.<TextField>lookup(LoginScene.NAME_INPUT).getText();
            int playerLvl = Integer.parseInt(loginScene.<TextField>lookup(LoginScene.LVL_INPUT).getText());
            Player player = new Player(playerName, playerLvl);

            setupView(player);    // Now that we have player, let's build the view
            switchScene(lobbySelectionScene, "Santorini Lobby Selection Screen");
        });

        lobbySelectionScene.<Button>lookup(LobbySelectionScene.CONNECT_BTN).setOnAction(e -> {
            try {
                internalView.connect(lobbySelectionScene.<TextField>lookup(LobbySelectionScene.IP_INPUT).getText());
                internalView.startNetworkThread();
                lobbySelectionScene.didConnect();
            } catch (IOException ex) {
                alert("Error", "Invalid IP");
            }
        });

        lobbySelectionScene.<Button>lookup(LobbySelectionScene.JOIN_BTN).setOnAction(e -> {
            TextField lobbyName = lobbySelectionScene.lookup(LobbySelectionScene.LOBBY_INPUT);
            if (!lobbyName.getText().trim().isEmpty()) {
                internalView.join(lobbyName.getText());
                switchScene(gameplayScene, "Santorini");
                mainStage.setMaximized(true);
            } else {
                alert("Error", "Lobby Name can't be empty");
            }
        });

        Button startBtn = gameplayScene.lookup(GameplayScene.START_BTN);
        startBtn.setOnAction(e -> {
            internalView.onCommand(new StartGameCommandMessage());
            startBtn.setVisible(false);
        });

        GridPane boardGrid = gameplayScene.lookup(GameplayScene.BOARD);
        boardGrid.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            for (Node node : boardGrid.getChildren()) {
                if (node instanceof Rectangle) {
                    if (node.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                        System.out.println("Node: " + node + " at " + GridPane.getRowIndex(node) + "/" + GridPane.getColumnIndex(node));
                    }
                }
            }
        });

        // Begin
        switchScene(loginScene,"Welcome to Santorini");
    }

    private void switchScene(SantoriniScene scene, String title) {
        mainStage.setTitle(title);
        mainStage.setScene(scene.getFXScene());
        mainStage.show();
    }

    private static void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupView(Player me) {
        internalView = new ClientRemoteView(me) {
            @Override
            public void onDisconnect() {}

            @Override
            public void onBoardUpdate(BoardUpdateMessage message) {}

            @Override
            public void onLobbiesUpdate(LobbiesUpdateMessage message) {
                Platform.runLater(() -> {
                    lobbySelectionScene.<Label>lookup(LobbySelectionScene.LOBBIES_LIST)
                        .setText("Available lobbies: " + String.join(", ", message.getLobbyNames()));
                });
            }

            @Override
            public void onNextActionsUpdate(NextActionsUpdateMessage message) {}

            @Override
            public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
                System.out.println("Player turn update");
                currentTurn = message.getPlayer();
            }

            @Override
            public void onPlayersUpdate(PlayersUpdateMessage message) {
                Platform.runLater(() -> {
                    VBox onlinePlayersLabel = gameplayScene.lookup(GameplayScene.PLAYER_LIST);
                    onlinePlayersLabel.getChildren().clear();

                    iAmTheHost = message.getPlayerList().get(0).equals(me);
                    if(iAmTheHost) {
                        gameplayScene.<Button>lookup(GameplayScene.START_BTN).setVisible(true);
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
                System.out.println("Show gods");
                Platform.runLater(() -> {
                    if (message.getHowManyToChoose() == 0 || message.getGods() == null) {
                        // Hide god selection and transparency layer, show grid
                        gameplayScene.<Node>lookup(GameplayScene.GOD_SELECTION_VIEW).setVisible(false);
                        gameplayScene.<Node>lookup(GameplayScene.TRANSPARENCY).setVisible(false);
                        gameplayScene.<Node>lookup(GameplayScene.BOARD).setVisible(true);
                    } else {
                        // Show it
                        gameplayScene.showAndPickGods(message.getGods(), me.equals(currentTurn) /* should pick */, message.getHowManyToChoose() /* how many */,
                            (chosenGods) -> { /* run when user selects */
                                if (message.getHowManyToChoose() > 1) {
                                    onCommand(new SetGodPoolCommandMessage(chosenGods));
                                } else {
                                    onCommand(new SetGodCommandMessage(chosenGods.get(0)));
                                }
                            });
                    }
                });
            }

            @Override
            public void onStorageUpdate(StorageUpdateMessage message) {}

            @Override
            public void onText(TextMessage message) {
                Platform.runLater(() -> {
                    textMessage.setText(message.getText());
                });
            }
        };
    }
}
