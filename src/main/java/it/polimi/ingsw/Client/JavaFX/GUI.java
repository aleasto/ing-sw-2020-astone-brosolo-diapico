package it.polimi.ingsw.Client.JavaFX;

import it.polimi.ingsw.Client.JavaFX.Scenes.GameplayScene;
import it.polimi.ingsw.Client.JavaFX.Scenes.LobbySelectionScene;
import it.polimi.ingsw.Client.JavaFX.Scenes.LoginScene;
import it.polimi.ingsw.Client.JavaFX.Scenes.SantoriniScene;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.GUIColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GUI extends Application {
    private ClientRemoteView internalView;
    private boolean iAmTheHost;
    private Player currentTurn;
    private Player myself;
    private Board board;
    private BoardClickState boardClickState;

    private Stage mainStage;
    private LoginScene loginScene;
    private LobbySelectionScene lobbySelectionScene;
    private GameplayScene gameplayScene;

    private final HashMap<Player, Color> colors = new HashMap<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);

        mainStage.setOnCloseRequest(e -> {
            if (internalView != null) {
                internalView.disconnect();
            }
        });

        // Create the three scenes
        loginScene = new LoginScene();
        lobbySelectionScene = new LobbySelectionScene();
        gameplayScene = new GameplayScene(colors);

        // Hook up the clickables
        loginScene.<Button>lookup(LoginScene.LOGIN_BTN).setOnAction(e -> {
            String playerName = loginScene.<TextField>lookup(LoginScene.NAME_INPUT).getText();
            int playerLvl;
            try {
                playerLvl = Integer.parseInt(loginScene.<TextField>lookup(LoginScene.LVL_INPUT).getText());
                Player player = new Player(playerName, playerLvl);
                setupView(player);    // Now that we have player, let's build the view
                switchScene(lobbySelectionScene, "Santorini Lobby Selection Screen");
            } catch (Exception ex) {
                loginScene.<TextField>lookup(LoginScene.LVL_INPUT).clear();
                alert("Error", "Try to keep it to positive integers");
            }
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

        gameplayScene.<Button>lookup(GameplayScene.END_TURN_BTN).setOnAction(e -> {
            internalView.onCommand(new EndTurnCommandMessage());
            boardClickState = new ChooseWorkerClickState();
        });

        gameplayScene.setBoardClickListener((x, y) -> {
            if (myself.equals(currentTurn)) {
                boardClickState.handleBoardClick(x, y);
            }
        });

        gameplayScene.<Button>lookup(GameplayScene.MOVE_BTN).setOnAction(e -> {
            boardClickState = new MoveClickState();
        });
        gameplayScene.<Button>lookup(GameplayScene.BUILD_BTN).setOnAction(e -> {
            boardClickState = new BuildClickState();
        });
        boardClickState = new PlaceWorkerState();

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
        myself = me;
        internalView = new ClientRemoteView(me) {
            @Override
            public void onPlayerChoseGodEvent(PlayerChoseGodEventMessage message) {
                if (getPlayer().equals(message.getPlayer())) {
                    Image image = new Image("/godcards/" + message.getGod() + ".png");
                    gameplayScene.<ImageView>lookup(GameplayScene.MY_GOD).setImage(image);
                    gameplayScene.<ImageView>lookup(GameplayScene.MY_GOD).setVisible(true);
                }
            }

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
                Platform.runLater(() -> {
                    board = message.getBoard();
                    for (int i = 0; i < board.getDimX(); i++) {
                        for (int j = 0; j < board.getDimY(); j++) {
                            StackPane tilePane = gameplayScene.lookup("#" + i + "" + j);
                            gameplayScene.drawTileInto(tilePane, board.getAt(i, j));
                        }
                    }
                });
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
                gameplayScene.<Button>lookup(GameplayScene.END_TURN_BTN).setVisible(true);
                boardClickState = new ChooseWorkerClickState();
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

                    iAmTheHost = message.getPlayerList().size() > 0 && message.getPlayerList().get(0).equals(me);
                    if (iAmTheHost) {
                        gameplayScene.<Button>lookup(GameplayScene.START_BTN).setVisible(true);
                    }

                    for (Player player : message.getPlayerList()) {
                        if (!colors.containsKey(player)) {
                            colors.put(player, GUIColor.uniqueColor());
                        }
                        Label label = new Label();
                        label.setText(player.getName());
                        Color color = colors.get(player);
                        if (color != null) {
                            label.setTextFill(color);
                        }
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
                        gameplayScene.endGodSelectionPhase();
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

    public class PlaceWorkerState implements BoardClickState {
        @Override
        public void handleBoardClick(int x, int y) {
            internalView.onCommand(new PlaceWorkerCommandMessage(x, y));
        }
    }

    private Tile startingTile;
    public class ChooseWorkerClickState implements BoardClickState {

        public ChooseWorkerClickState() {
            gameplayScene.<Node>lookup(GameplayScene.ACTIONS_BOX).setVisible(false);
        }

        @Override
        public void handleBoardClick(int x, int y) {
            Tile tile = board.getAt(x, y);
            if (tile.getOccupant() != null && tile.getOccupant().getOwner().equals(myself)) {
                startingTile = tile;
                Node actionsBox = gameplayScene.lookup(GameplayScene.ACTIONS_BOX);
                Node tileNode = gameplayScene.lookup("#" + x + "" + y);
                actionsBox.setTranslateX(tileNode.getLayoutX() - actionsBox.getLayoutX());
                actionsBox.setTranslateY(tileNode.getLayoutY() - actionsBox.getLayoutY());
                actionsBox.setVisible(true);
                /*
                if cannot move
                    gameplayScene.<Button>lookup(GameplayScene.MOVE_BTN).setManaged(false);   (o setDisable(true))
                if cannot build
                    gameplayScene.<Button>lookup(GameplayScene.BUILD_BTN).setManaged(false);  (o setDisable(true))
                 */
            }
        }
    }

    public class MoveClickState implements BoardClickState {
        @Override
        public void handleBoardClick(int x, int y) {
            internalView.onCommand(new MoveCommandMessage(startingTile.getX(), startingTile.getY(), x, y));
            boardClickState = new ChooseWorkerClickState();
        }
    }

    public class BuildClickState implements BoardClickState {
        @Override
        public void handleBoardClick(int x, int y) {
            int block = board.getAt(x, y).getHeight(); // TODO: How do we choose to build a dome?
            internalView.onCommand(new BuildCommandMessage(startingTile.getX(), startingTile.getY(), x, y, block));
            boardClickState = new ChooseWorkerClickState();
        }
    }
}
