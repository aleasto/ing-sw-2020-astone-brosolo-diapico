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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFX extends Application {
    private ClientRemoteView internalView;
    private boolean iAmTheHost;
    private Player currentTurn;
    private Player myPlayer;

    private Stage mainStage;
    private LoginScene loginScene;
    private LobbySelectionScene lobbySelectionScene;
    private GameplayScene gameplayScene;

    private HashMap<Player, Color> colors = new HashMap<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);

        mainStage.setOnCloseRequest(e -> {
            internalView.disconnect();
        });

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

        gameplayScene.setBoardClickListener((x, y) -> {
            /*if(myPlayer.equals(currentTurn)) {
                System.out.println("Clicked node at " + x + "/" + y);
            } else {
                System.out.println("Not your turn");
            }*/
            internalView.onCommand(new PlaceWorkerCommandMessage(x, y));
        });

        // Begin
        switchScene(loginScene, "Welcome to Santorini");
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
        myPlayer = me;
        internalView = new ClientRemoteView(me) {
            @Override
            public void onPlayerLoseEvent(PlayerLoseEventMessage message) {
            }

            @Override
            public void onEndGameEvent(EndGameEventMessage message) {
            }

            @Override
            public void onDisconnect() {
                reset("Connection dropped.");
            }

            @Override
            public void onBoardUpdate(BoardUpdateMessage message) {
            }

            @Override
            public void onLobbiesUpdate(LobbiesUpdateMessage message) {
                Platform.runLater(() -> {
                    lobbySelectionScene.<Label>lookup(LobbySelectionScene.LOBBIES_LIST)
                            .setText("Available lobbies: " + message.getLobbies().stream().map(LobbyInfo::getName)
                                    .collect(Collectors.joining(", ")));
                });
            }

            @Override
            public void onNextActionsUpdate(NextActionsUpdateMessage message) {
            }

            @Override
            public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
                currentTurn = message.getPlayer();
            }

            @Override
            public void onPlayersUpdate(PlayersUpdateMessage message) {
                Platform.runLater(() -> {
                    VBox onlinePlayersLabel = gameplayScene.lookup(GameplayScene.PLAYER_LIST);
                    onlinePlayersLabel.getChildren().clear();

                    iAmTheHost = message.getPlayerList().get(0).equals(me);
                    if (iAmTheHost) {
                        gameplayScene.<Button>lookup(GameplayScene.START_BTN).setVisible(true);
                    }

                    for(Player player : message.getPlayerList()) {
                        if (!colors.containsKey(player)) {
                            colors.put(player, GUIColor.uniqueColor());
                        }
                        Label label = new Label();
                        label.setText(player.getName());
                        label.setTextFill(colors.get(player));
                        label.setFont(Font.font(label.getFont().toString(), FontWeight.BOLD, 15));
                        onlinePlayersLabel.getChildren().add(label);
                    }
                });
            }

            @Override
            public void onShowGods(GodListMessage message) {
                Platform.runLater(() -> {
                    if (message.getHowManyToChoose() == 0 || message.getGods() == null) {
                        // Hide god selection and transparency layer, show grid
                        gameplayScene.<Node>lookup(GameplayScene.GOD_SELECTION_VIEW).setVisible(false);
                        gameplayScene.<Node>lookup(GameplayScene.TRANSPARENCY).setVisible(false);
                        gameplayScene.<Node>lookup(GameplayScene.BOARD).setVisible(true);
                        gameplayScene.<Node>lookup(GameplayScene.GAME_LABEL).setVisible(true);
                    } else {
                        // Show it
                        gameplayScene.showAndPickGods(message.getGods(), me.equals(currentTurn) /* should pick */, message.getHowManyToChoose() /* how many */,
                                (chosenGods) -> { /* run when user selects */
                                    if (message.getHowManyToChoose() > 1) {
                                        onCommand(new SetGodPoolCommandMessage(chosenGods));
                                    } else {
                                        Image image = new Image("/godcards/" + chosenGods.get(0) + ".png");
                                        gameplayScene.<ImageView>lookup(GameplayScene.MY_GOD).setImage(image);
                                        gameplayScene.<ImageView>lookup(GameplayScene.MY_GOD).setVisible(true);
                                        onCommand(new SetGodCommandMessage(chosenGods.get(0)));
                                    }
                                });
                    }
                });
            }

            @Override
            public void onStorageUpdate(StorageUpdateMessage message) {
            }

            @Override
            public void onText(TextMessage message) {
                Platform.runLater(() -> {
                    gameplayScene.<Label>lookup(GameplayScene.GOD_SELECTION_LABEL).setText(message.getText());
                    gameplayScene.<Label>lookup(GameplayScene.GAME_LABEL).setText(message.getText());
                });
            }
        };
    }

    public void reset(String message) {
        Platform.runLater(() -> {
            gameplayScene.<Label>lookup(GameplayScene.GOD_SELECTION_LABEL).setText(message);
            gameplayScene.<Label>lookup(GameplayScene.GAME_LABEL).setText(message);
        });
        colors.clear();
        GUIColor.reset();
    }
}
