package it.polimi.ingsw.Client.JavaFX;

import it.polimi.ingsw.Client.JavaFX.Scenes.GameplayScene;
import it.polimi.ingsw.Client.JavaFX.Scenes.LobbySelectionScene;
import it.polimi.ingsw.Client.JavaFX.Scenes.LoginScene;
import it.polimi.ingsw.Client.JavaFX.Scenes.SantoriniScene;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.GUIColor;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GUI extends Application {
    private ConfReader confReader = null;

    private ClientRemoteView internalView;
    private Player myself;
    private Player currentTurn;
    private Board board;
    private Storage storage;

    private List<MoveCommandMessage> nextMoves = new ArrayList<>();
    private List<BuildCommandMessage> nextBuilds = new ArrayList<>();
    private List<Rectangle> highlightMoves = new ArrayList<>();
    private List<Rectangle> highlightBuilds = new ArrayList<>();
    private Tile startingTile;
    private List<MoveCommandMessage> nextMovesFromStartingTile;
    private List<BuildCommandMessage> nextBuildsFromStartingTile;

    private Stage mainStage;
    private LoginScene loginScene;
    private LobbySelectionScene lobbySelectionScene;
    private GameplayScene gameplayScene;
    private BoardClickState boardClickState;
    private boolean closing = false;

    private final HashMap<Player, Color> colors = new HashMap<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage mainStage) {
        try {
            confReader = new ConfReader("gui.conf");
        } catch (IOException e) {
            alert("Error", e.getMessage());
            System.exit(1);
        }

        this.mainStage = mainStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);

        mainStage.setOnCloseRequest(e -> {
            closing = true;
            if (internalView != null) {
                internalView.disconnect();
            }
        });

        // Create the three scenes
        loginScene = new LoginScene();
        lobbySelectionScene = new LobbySelectionScene(confReader);
        gameplayScene = new GameplayScene(confReader, colors);

        // Hook up the clickables
        setupClickEvents();

        // Begin
        switchScene(loginScene, "Welcome to Santorini");
    }

    private void setupClickEvents() {
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
            String host = lobbySelectionScene.<TextField>lookup(LobbySelectionScene.IP_INPUT).getText();
            int port = Integer.parseInt(lobbySelectionScene.<TextField>lookup(LobbySelectionScene.PORT_INPUT).getText());
            try {
                internalView.connect(host, port);
                internalView.startNetworkThread();
                lobbySelectionScene.didConnect();
            } catch (IOException ex) {
                alert("Error", "No server at " + host + ":" + port);
            }
        });

        lobbySelectionScene.<Button>lookup(LobbySelectionScene.JOIN_BTN).setOnAction(e -> {
            TextField lobbyName = lobbySelectionScene.lookup(LobbySelectionScene.LOBBY_INPUT);
            if (!lobbyName.getText().trim().isEmpty()) {
                internalView.join(lobbyName.getText());
                switchScene(gameplayScene, "Santorini");
                mainStage.setMaximized(true);
                // Workaround Windows not showing [-] button
                gameplayScene.<HBox>lookup(GameplayScene.BLOCKS_NUM_CHOICE).requestLayout();
            } else {
                alert("Error", "Lobby Name can't be empty");
            }
        });

        TableView<LobbyInfo> lobbiesTable = lobbySelectionScene.lookup(LobbySelectionScene.LOBBIES_LIST);
        lobbiesTable.setOnMouseClicked(e -> {
            LobbyInfo lobby = lobbiesTable.getSelectionModel().getSelectedItem();
            if (e.getClickCount() == 2 && lobby != null) {
                internalView.join(lobby.getName());
                switchScene(gameplayScene, "Santorini");
                mainStage.setMaximized(true);
            }
        });


        gameplayScene.<Button>lookup(GameplayScene.START_BTN).setOnAction(e -> {
            gameplayScene.<Node>lookup(GameplayScene.START_VIEW).setVisible(false);

            GameRules rules = new GameRules();
            rules.setPlayWithGods(gameplayScene.<CheckBox>lookup(GameplayScene.GODS_OPT_CHECKBOX).isSelected());
            rules.setBoardSize(new Pair<>(
                    gameplayScene.<ChoiceBox<Integer>>lookup(GameplayScene.BOARD_DIM_X_CHOICE).getValue(),
                    gameplayScene.<ChoiceBox<Integer>>lookup(GameplayScene.BOARD_DIM_Y_CHOICE).getValue()));
            rules.setWorkers(gameplayScene.<ChoiceBox<Integer>>lookup(GameplayScene.WORKERS_NUM_CHOICE).getValue());
            Integer[] blocks = gameplayScene.<MyNumberSpinnerCollection>lookup(GameplayScene.BLOCKS_NUM_CHOICE).getSpinners()
                    .stream().map(Spinner::getValue).toArray(Integer[]::new);
            rules.setBlocks(blocks);
            internalView.onCommand(new StartGameCommandMessage(rules));
        });

        gameplayScene.<Button>lookup(GameplayScene.END_TURN_BTN).setOnAction(e -> {
            internalView.onCommand(new EndTurnCommandMessage());
            boardClickState = new ChooseWorkerClickState();
        });

        gameplayScene.<Button>lookup(GameplayScene.MOVE_BTN).setOnAction(e -> {
            //Highlights available moves
            for(MoveCommandMessage availableMoves : nextMovesFromStartingTile) {
                Rectangle highlight = new Rectangle(gameplayScene.getTileSize(), gameplayScene.getTileSize());
                highlight.setFill(Color.rgb(25, 175, 240, 0.75));
                StackPane tilePane = gameplayScene.lookup("#" + availableMoves.getToX() + "" + availableMoves.getToY());
                tilePane.getChildren().add(highlight);
                highlightMoves.add(highlight);
            }
            gameplayScene.<VBox>lookup(GameplayScene.ACTIONS_BOX).setVisible(false);
            boardClickState = new MoveClickState();
        });
        gameplayScene.<Button>lookup(GameplayScene.BUILD_BTN).setOnAction(e -> {
            //Highlights available builds
            for(BuildCommandMessage availableBuilds : nextBuildsFromStartingTile) {
                Rectangle highlight = new Rectangle(gameplayScene.getTileSize(), gameplayScene.getTileSize());
                highlight.setFill(Color.rgb(240, 130, 0, 0.75));
                StackPane tilePane = gameplayScene.lookup("#" + availableBuilds.getToX() + "" + availableBuilds.getToY());
                tilePane.getChildren().add(highlight);
                highlightBuilds.add(highlight);
            }
            gameplayScene.<VBox>lookup(GameplayScene.ACTIONS_BOX).setVisible(false);
            boardClickState = new BuildClickState();
        });
        boardClickState = new PlaceWorkerState();
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
                    Platform.runLater(() -> {
                        Image image = new Image("/godcards/" + message.getGod().getName() + ".png");
                        StackPane myGod = gameplayScene.lookup(GameplayScene.MY_GOD);
                        ImageView myGodImage = (ImageView) myGod.getChildren().get(0);
                        myGodImage.setImage(image);
                        Label myGodDesc = (Label) myGod.getChildren().get(2);
                        myGodDesc.setText(message.getGod().getDescription());
                        myGod.setVisible(true);
                    });
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
                if (!closing) {
                    reset();
                    Platform.runLater(() -> {
                        alert("Notice", "You have been disconnected.");
                        lobbySelectionScene = new LobbySelectionScene(confReader); // This is necessary since the layout changes
                        gameplayScene = new GameplayScene(confReader, colors);
                        setupClickEvents();

                        mainStage.setMaximized(false);
                        switchScene(lobbySelectionScene, "Santorini Lobby Selection Screen");
                    });
                }
            }

            @Override
            public void onBoardUpdate(BoardUpdateMessage message) {
                if (board == null) {
                    // if board was null, generate the board structure
                    Platform.runLater(() -> {
                        gameplayScene.createBoard(message.getBoard().getDimX(), message.getBoard().getDimY(), (x, y) -> {
                            if (me.equals(currentTurn)) {
                                boardClickState.handleBoardClick(x, y);
                            }
                        });
                    });
                }
                board = message.getBoard();
                Platform.runLater(() -> {
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
                Set<LobbyInfo> lobbies = message.getLobbies();
                Platform.runLater(() -> {
                    TableView<LobbyInfo> tableView = lobbySelectionScene.lookup(LobbySelectionScene.LOBBIES_LIST);
                    tableView.getItems().clear();
                    tableView.getItems().addAll(lobbies);
                });
            }

            @Override
            public void onNextActionsUpdate(NextActionsUpdateMessage message) {
                nextMoves = message.getNextMoves();
                nextBuilds = message.getNextBuilds();
                Button endTurnBtn = gameplayScene.lookup(GameplayScene.END_TURN_BTN);
                endTurnBtn.setVisible(true);
                endTurnBtn.setDisable(message.mustMove() || message.mustBuild());
                boardClickState = new ChooseWorkerClickState();
            }

            @Override
            public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
                currentTurn = message.getPlayer();
                gameplayScene.<Node>lookup(GameplayScene.END_TURN_BTN).setDisable(true);
                Platform.runLater(() -> {
                    gameplayScene.updatePlayers(currentTurn);
                });
            }

            @Override
            public void onPlayersUpdate(PlayersUpdateMessage message) {
                Platform.runLater(() -> {
                    boolean iAmTheHost = message.getPlayerList().size() > 0 && message.getPlayerList().get(0).equals(me);
                    if (iAmTheHost) {
                        gameplayScene.<Node>lookup(GameplayScene.START_VIEW).setVisible(true);
                    }
                    for (Player player : message.getPlayerList()) {
                        if (!colors.containsKey(player)) {
                            colors.put(player, GUIColor.uniqueColor());
                        }
                    }
                    gameplayScene.updatePlayers(currentTurn);
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
                if(storage == null) {
                    Platform.runLater(() -> {
                        gameplayScene.createStorage(message.getStorage());
                    });
                }
                storage = message.getStorage();
                Platform.runLater(() -> {
                    gameplayScene.updateStorage(message.getStorage());
                });
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

    public void reset() {
        closing = false;
        currentTurn = null;
        board = null;
        nextMoves = new ArrayList<>();
        nextBuilds = new ArrayList<>();
        startingTile = null;
        nextMovesFromStartingTile = null;
        nextBuildsFromStartingTile = null;
        highlightMoves = new ArrayList<>();
        highlightBuilds = new ArrayList<>();
        colors.clear();
        GUIColor.reset();
    }

    public class PlaceWorkerState implements BoardClickState {
        @Override
        public void handleBoardClick(int x, int y) {
            internalView.onCommand(new PlaceWorkerCommandMessage(x, y));
        }
    }

    public class ChooseWorkerClickState implements BoardClickState {

        public ChooseWorkerClickState() {
            gameplayScene.<Node>lookup(GameplayScene.ACTIONS_BOX).setVisible(false);
            gameplayScene.<Node>lookup(GameplayScene.BUILDS_BOX).setVisible(false);

            if(nextMovesFromStartingTile != null) {
                for(MoveCommandMessage availableMoves : nextMovesFromStartingTile) {
                    StackPane tilePane = gameplayScene.lookup("#" + availableMoves.getToX() + "" + availableMoves.getToY());
                    tilePane.getChildren().removeAll(highlightMoves);
                }
                highlightMoves.clear();
            }
            if(nextBuildsFromStartingTile != null) {
                for(BuildCommandMessage availableBuilds : nextBuildsFromStartingTile) {
                    StackPane tilePane = gameplayScene.lookup("#" + availableBuilds.getToX() + "" + availableBuilds.getToY());
                    tilePane.getChildren().removeAll(highlightBuilds);
                }
                highlightBuilds.clear();
            }
        }

        @Override
        public void handleBoardClick(int x, int y) {
            Node actionsBox = gameplayScene.lookup(GameplayScene.ACTIONS_BOX);
            Tile tile = board.getAt(x, y);
            if (tile.getOccupant() != null && tile.getOccupant().getOwner().equals(myself)) {
                startingTile = tile;
                Node tileNode = gameplayScene.lookup("#" + x + "" + y);
                Node boardNode = gameplayScene.lookup(GameplayScene.BOARD);
                actionsBox.setTranslateX(tileNode.getLayoutX() + boardNode.getLayoutX() - actionsBox.getLayoutX());
                actionsBox.setTranslateY(tileNode.getLayoutY() + boardNode.getLayoutY() - actionsBox.getLayoutY());

                nextMovesFromStartingTile = nextMoves.stream().filter(
                        m -> m.getFromX() == startingTile.getX() && m.getFromY() == startingTile.getY()).collect(Collectors.toList());
                nextBuildsFromStartingTile = nextBuilds.stream().filter(
                        m -> m.getFromX() == startingTile.getX() && m.getFromY() == startingTile.getY()).collect(Collectors.toList());

                gameplayScene.<Button>lookup(GameplayScene.MOVE_BTN).setDisable(nextMovesFromStartingTile.size() == 0);
                gameplayScene.<Button>lookup(GameplayScene.BUILD_BTN).setDisable(nextBuildsFromStartingTile.size() == 0);

                actionsBox.setVisible(true);
            } else {
                actionsBox.setVisible(false);
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
            List<BuildCommandMessage> availBuilds = nextBuilds.stream().filter(m ->
                    m.getFromX() == startingTile.getX() && m.getFromY() == startingTile.getY() &&
                            m.getToX() == x && m.getToY() == y).collect(Collectors.toList());
            if (availBuilds.size() > 1) {
                VBox buildBox = gameplayScene.lookup(GameplayScene.BUILDS_BOX);
                buildBox.getChildren().clear();
                for (BuildCommandMessage build : availBuilds) {
                    String buttonText = build.getBlock() == board.getMaxHeight() ? "Dome" : "Level " + build.getBlock();
                    Button buildBtn = new Button(buttonText);
                    buildBtn.setMaxWidth(Double.MAX_VALUE);
                    buildBtn.setMaxHeight(Double.MAX_VALUE);
                    buildBtn.setOnAction(e -> {
                        doBuild(x, y, build.getBlock());
                    });
                    buildBox.getChildren().add(buildBtn);
                    VBox.setVgrow(buildBtn, Priority.ALWAYS);
                }
                Node tileNode = gameplayScene.lookup("#" + x + "" + y);
                Node boardNode = gameplayScene.lookup(GameplayScene.BOARD);
                buildBox.setTranslateX(tileNode.getLayoutX() + boardNode.getLayoutX() - buildBox.getLayoutX());
                buildBox.setTranslateY(tileNode.getLayoutY() + boardNode.getLayoutY() - buildBox.getLayoutY());
                buildBox.setVisible(true);
                gameplayScene.<Node>lookup(GameplayScene.ACTIONS_BOX).setVisible(false);
            } else {
                doBuild(x, y, board.getAt(x, y).getHeight());
            }
        }

        private void doBuild(int x, int y, int height) {
            internalView.onCommand(new BuildCommandMessage(startingTile.getX(), startingTile.getY(), x, y, height));
            boardClickState = new ChooseWorkerClickState();
        }
    }
}
