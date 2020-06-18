package it.polimi.ingsw.Client.JavaFX.Scenes;

import it.polimi.ingsw.Client.JavaFX.*;
import it.polimi.ingsw.Game.Actions.GodInfo;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Storage;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.FXUtils;
import it.polimi.ingsw.Utils.Pair;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class GameplayScene extends SantoriniScene {

    public static final String MAIN_STACK = "#main_stack";
    public static final String START_VIEW = "#start_view";
    public static final String START_BTN = "#start_btn";
    public static final String START_VIEW_LABEL = "#start_label";
    public static final String GODS_OPT_CHECKBOX = "#gods_opt_checkbox";
    public static final String BOARD_DIM_X_CHOICE = "#board_dim_x";
    public static final String BOARD_DIM_Y_CHOICE = "#board_dim_y";
    public static final String WORKERS_NUM_CHOICE = "#workers_num";
    public static final String BLOCKS_NUM_CHOICE = "#block_num";
    public static final String PLAYER_LIST = "#player_list";
    public static final String PLAYER_LIST_PANE = "#player_list_pane";
    public static final String SELECT_GODS_BTN = "#select_gods_btn";
    public static final String GOD_LIST = "#god_list";
    public static final String GOD_SELECTION_VIEW = "#god_selection_view";
    public static final String TRANSPARENCY = "#transparency";
    public static final String STORAGE = "#storage";
    public static final String BOARD = "#board";
    public static final String GOD_SELECTION_LABEL = "#god_selection_label";
    public static final String GAME_LABEL = "#game_label";
    public static final String MY_GOD_BOX = "#my_god";
    public static final String MY_GOD_IMAGE = "#my_god_image";
    public static final String MY_GOD_NAME = "#my_god_name";
    public static final String MY_GOD_DESC = "#my_god_desc";
    public static final String END_TURN_BTN = "#end_turn_btn";
    public static final String MOVE_BTN = "#move_btn";
    public static final String BUILD_BTN = "#build_btn";
    public static final String ACTIONS_BOX = "#actions_box";
    public static final String BUILDS_BOX = "#builds_box";
    public static final String RECT_PREFIX = "#rect_";
    public static final String LEVEL_LABEL_PREFIX = "#level";

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double width = screenSize.getWidth();
    private final double height = screenSize.getHeight();
    private final HashMap<Player, Color> colors;

    private final Scene scene;

    private static final boolean DEFAULT_PLAY_WITH_GODS = true;
    private static final int DEFAULT_BOARD_SIZE_X = 5;
    private static final int DEFAULT_BOARD_SIZE_Y = 5;
    private static final Integer[] DEFAULT_BLOCKS = { 22, 18, 14, 14 };
    private static final int DEFAULT_WORKERS = 2;

    @Override
    public Scene getFXScene() {
        return scene;
    }

    public GameplayScene(ConfReader confReader, HashMap<Player, Color> colors) {
        this.colors = colors;

        Image background = new Image("SantoriniBoard.png", width, height, true, true);

        // Stack up different panes (background, board, godpool, etc...)
        StackPane stack = new StackPane(new ImageView(background));
        stack.setId(SET_ID(MAIN_STACK));

        // Storage
        VBox storageDisplay = new VBox(20);
        StackPane.setAlignment(storageDisplay, Pos.TOP_LEFT);
        storageDisplay.setId(SET_ID(STORAGE));
        storageDisplay.setMaxSize(width / 7.5d, VBox.USE_PREF_SIZE);
        storageDisplay.setStyle("-fx-background-color: #008080;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 3;" +
                "-fx-border-style: dashed;");
        stack.getChildren().add(storageDisplay);

        // Transparency
        Rectangle transparency = new Rectangle(width, height);
        transparency.setFill(Color.rgb(0, 0, 0, 0.90));
        transparency.setId(SET_ID(TRANSPARENCY));
        stack.getChildren().add(transparency);

        // Anchor pane for Players, God card, Game guide label, EndTurn Button
        VBox playerListContainer = new VBox(10);
        playerListContainer.setPrefWidth(width / 9);
        playerListContainer.setPrefHeight(height / 2);
        playerListContainer.setId(SET_ID(PLAYER_LIST));
        playerListContainer.setAlignment(Pos.TOP_LEFT);

        StackPane myGod = new StackPane();
        myGod.setVisible(false);
        myGod.setId(SET_ID(MY_GOD_BOX));
        ImageView myGodImage = new ImageView();
        myGodImage.setId(SET_ID(MY_GOD_IMAGE));
        myGodImage.setFitHeight(height / 2);
        myGodImage.setFitWidth(width / 6.4);
        VBox myGodInfoBox = new VBox(1);
        myGodInfoBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        HBox myGodNameContainer = new HBox(0);
        myGodNameContainer.setAlignment(Pos.CENTER);
        Label myGodName = new Label();
        myGodName.setId(SET_ID(MY_GOD_NAME));
        myGodName.setTextFill(Color.WHITE);
        myGodNameContainer.getChildren().add(myGodName);
        Label myGodDesc = new Label();
        myGodDesc.setId(SET_ID(MY_GOD_DESC));
        myGodDesc.setWrapText(true);
        myGodDesc.maxWidthProperty().bind(myGodImage.fitWidthProperty().divide(2));
        myGodDesc.setTextFill(Color.WHITE);
        myGodInfoBox.getChildren().addAll(myGodNameContainer, myGodDesc);
        myGodInfoBox.setVisible(false);
        Rectangle descShade = new Rectangle();
        descShade.setFill(Color.rgb(0, 0, 0, 0.7));
        descShade.widthProperty().bind(myGodInfoBox.widthProperty().add(10));
        descShade.heightProperty().bind(myGodInfoBox.heightProperty().add(10));
        descShade.setVisible(false);

        myGod.getChildren().addAll(myGodImage, descShade, myGodInfoBox);
        myGod.hoverProperty().addListener(((observableValue, oldValue, newValue) -> {
            descShade.setVisible(newValue);
            myGodInfoBox.setVisible(newValue);
        }));

        Label gameGuide = new Label("");
        gameGuide.setId(SET_ID(GAME_LABEL));
        gameGuide.setVisible(false);
        gameGuide.setMaxWidth(Double.MAX_VALUE);
        gameGuide.setAlignment(Pos.CENTER);
        FXUtils.embellishLabel(gameGuide, Color.BLACK, 15);

        ImageView onlinePlayersBG = new ImageView(new Image("playerbox.png", width / 6.4, height / 2, true, true));

        AnchorPane mainAnchorPane = new AnchorPane(onlinePlayersBG, playerListContainer, myGod, gameGuide);

        AnchorPane.setTopAnchor(onlinePlayersBG, 1d);
        AnchorPane.setRightAnchor(onlinePlayersBG, 1d);

        AnchorPane.setTopAnchor(playerListContainer, height / 12d);
        AnchorPane.setLeftAnchor(playerListContainer, width - (width / 7.3d));

        AnchorPane.setBottomAnchor(myGod, 1d);
        AnchorPane.setRightAnchor(myGod, 1d);

        AnchorPane.setBottomAnchor(gameGuide, height / 60d);
        AnchorPane.setLeftAnchor(gameGuide, 0d);
        AnchorPane.setRightAnchor(gameGuide, 0d);

        mainAnchorPane.setId(SET_ID(PLAYER_LIST_PANE));
        stack.getChildren().add(mainAnchorPane);

        // Start button and options
        VBox startView = new VBox(30);
        startView.setAlignment(Pos.CENTER);
        startView.setVisible(false);
        startView.setId(SET_ID(START_VIEW));

        HBox startOptionsRow1 = new HBox(width / 50);
        startOptionsRow1.setAlignment(Pos.CENTER);
        CheckBox godsOpt = new CheckBox("Play with gods");
        godsOpt.setStyle("-fx-text-fill: white");
        godsOpt.setSelected(confReader.getBoolean("play_with_gods", DEFAULT_PLAY_WITH_GODS));
        godsOpt.setId(SET_ID(GODS_OPT_CHECKBOX));

        HBox boardDimOpt = new HBox(1);
        boardDimOpt.setAlignment(Pos.CENTER);
        Label boardSizeOptionLabel = new Label(" board");
        boardSizeOptionLabel.setTextFill(Color.WHITE);
        Label boardSizeSeparatorLabel = new Label("x");
        boardSizeSeparatorLabel.setTextFill(Color.WHITE);
        ChoiceBox<Integer> boardDimX = new ChoiceBox<>();
        boardDimX.setId(SET_ID(BOARD_DIM_X_CHOICE));
        ChoiceBox<Integer> boardDimY = new ChoiceBox<>();
        boardDimY.setId(SET_ID(BOARD_DIM_Y_CHOICE));
        IntStream.rangeClosed(3, 5).forEach(i -> {
            boardDimX.getItems().add(i);
            boardDimY.getItems().add(i);
        });
        Pair<Integer, Integer> defaultBoardSize = confReader.getIntPair("board_size", DEFAULT_BOARD_SIZE_X, DEFAULT_BOARD_SIZE_Y);
        boardDimX.setValue(defaultBoardSize.getFirst());
        boardDimY.setValue(defaultBoardSize.getSecond());
        boardDimOpt.getChildren().addAll(boardDimX, boardSizeSeparatorLabel, boardDimY, boardSizeOptionLabel);

        HBox workersOpt = new HBox(1);
        workersOpt.setAlignment(Pos.CENTER);
        Label workersLabel = new Label(" workers");
        workersLabel.setTextFill(Color.WHITE);
        ChoiceBox<Integer> workersNum = new ChoiceBox<>();
        workersNum.setId(SET_ID(WORKERS_NUM_CHOICE));
        IntStream.rangeClosed(1,5).forEach(i -> workersNum.getItems().add(i));
        workersNum.setValue(confReader.getInt("workers", DEFAULT_WORKERS));
        workersOpt.getChildren().addAll(workersNum, workersLabel);
        startOptionsRow1.getChildren().addAll(godsOpt, boardDimOpt, workersOpt);

        HBox startOptionsRow2 = new HBox(10);
        startOptionsRow2.setAlignment(Pos.CENTER);
        Label blocksLabel = new Label("Blocks: ");
        blocksLabel.setTextFill(Color.WHITE);
        MyNumberSpinnerCollection blocks
                = new MyNumberSpinnerCollection(confReader.getInts("blocks", DEFAULT_BLOCKS));
        blocks.setId(SET_ID(BLOCKS_NUM_CHOICE));
        startOptionsRow2.getChildren().addAll(blocksLabel, blocks);

        Button startTheGame = new Button("Start!");
        startTheGame.setId(SET_ID(START_BTN));
        Label startViewResponseLabel = new Label();
        FXUtils.embellishLabel(startViewResponseLabel, Color.WHITE, 15);
        startViewResponseLabel.setId(SET_ID(START_VIEW_LABEL));
        startView.getChildren().addAll(startOptionsRow1, startOptionsRow2, startTheGame, startViewResponseLabel);
        stack.getChildren().add(startView);

        // God selection
        Label godSelectionTip = new Label("Tip: Left Click to Select, Right Click to Deselect");
        FXUtils.embellishLabel(godSelectionTip, Color.WHITE, 25);
        Label godSelectionGuide = new Label("");
        godSelectionGuide.setId(SET_ID(GOD_SELECTION_LABEL));
        FXUtils.embellishLabel(godSelectionGuide, Color.WHITE, 35);
        Button selectGodsBtn = new Button("Select");
        selectGodsBtn.setDisable(true);
        selectGodsBtn.setId(SET_ID(SELECT_GODS_BTN));
        HBox godsPlayable = new HBox(20);
        godsPlayable.setAlignment(Pos.CENTER);
        godsPlayable.setId(SET_ID(GOD_LIST));
        VBox godSelectionView = new VBox(2);
        godSelectionView.getChildren().addAll(godSelectionTip, godSelectionGuide, godsPlayable, selectGodsBtn);
        godSelectionView.setAlignment(Pos.CENTER);
        godSelectionView.setSpacing(45);
        godSelectionView.setId(SET_ID(GOD_SELECTION_VIEW));
        godSelectionView.setVisible(false);
        stack.getChildren().add(godSelectionView);

        // Board grid
        GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setPadding(new Insets(height / 60, 0, 0, 0));
        boardGrid.setHgap(getTileMargin());
        boardGrid.setVgap(getTileMargin());
        boardGrid.setId(SET_ID(BOARD));
        boardGrid.setVisible(false);
        // Board will be filled on the first boardUpdate, as that's the first time we know the board dimensions
        stack.getChildren().add(boardGrid);

        Button endTurn = new Button("End Turn");
        endTurn.setVisible(false);
        endTurn.setId(SET_ID(END_TURN_BTN));
        endTurn.setPrefSize(getTileSize(), getTileSize());
        StackPane.setAlignment(endTurn, Pos.CENTER_LEFT);
        endTurn.translateXProperty().bind(boardGrid.layoutXProperty().subtract(getTileSize() + getTileMargin()));
        stack.getChildren().add(endTurn);

        VBox actionsBox = new VBox(1);
        actionsBox.setVisible(false);
        actionsBox.setId(SET_ID(ACTIONS_BOX));
        actionsBox.setMaxSize(getTileSize(), getTileSize());
        Button moveButton = new Button("MOVE");
        moveButton.setId(SET_ID(MOVE_BTN));
        moveButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(moveButton, Priority.ALWAYS);
        Button buildButton = new Button("BUILD");
        buildButton.setId(SET_ID(BUILD_BTN));
        buildButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(buildButton, Priority.ALWAYS);
        actionsBox.getChildren().addAll(moveButton, buildButton);
        stack.getChildren().add(actionsBox);

        VBox buildsBox = new VBox(1);
        buildsBox.setVisible(false);
        buildsBox.setId(SET_ID(BUILDS_BOX));
        buildsBox.setMaxSize(getTileSize(), getTileSize());
        stack.getChildren().add(buildsBox);

        this.scene = new Scene(stack, width, height);
    }

    /**
     * Notify the scene that the game has started
     */
    public void onGameStart() {
        this.<Node>lookup(START_VIEW).setVisible(false);
    }

    /**
     * Show the user a list of gods, and prompts to pick a number of them
     * @param gods the list of gods
     * @param shouldPick should we pick, or display only
     * @param howMany how many to pick
     * @param selectAction the callback after having picked
     */
    public void showAndPickGods(List<GodInfo> gods, boolean shouldPick, int howMany, GodSelectionListener selectAction) {
        HBox godsPlayable = lookup(GOD_LIST);
        Button selectGodsBtn = lookup(SELECT_GODS_BTN);
        List<String> chosenGods = new ArrayList<>();

        selectGodsBtn.setVisible(shouldPick);
        selectGodsBtn.setDisable(true);

        godsPlayable.getChildren().clear();
        for (GodInfo godInfo : gods) {
            String god = godInfo.getName();

            //Create the god image
            Image image = new Image(getGodImageResourceFor(god), width / 15, height * 0.4, true, true);
            ImageView imageView = new ImageView(image);
            VBox godInfoBox = new VBox(1);
            godInfoBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
            HBox nameContainer = new HBox(0);
            nameContainer.setAlignment(Pos.CENTER);
            Label name = new Label();
            name.setTextFill(Color.WHITE);
            name.setText(godInfo.getName());
            nameContainer.getChildren().add(name);
            Label desc = new Label();
            desc.setWrapText(true);
            desc.maxWidthProperty().bind(image.widthProperty().subtract(5));
            desc.setTextFill(Color.WHITE);
            desc.setText(godInfo.getDescription());
            godInfoBox.getChildren().addAll(nameContainer, desc);
            godInfoBox.setVisible(false);

            Rectangle rect = new Rectangle();
            rect.widthProperty().bind(godInfoBox.widthProperty().add(5));
            rect.heightProperty().bind(godInfoBox.heightProperty().add(5));
            rect.setFill(Color.rgb(0, 0, 0, 0.7));
            rect.setVisible(false);

            StackPane godStack = new StackPane();
            godStack.getChildren().addAll(imageView, rect, godInfoBox);
            godStack.hoverProperty().addListener((observable, oldValue, newValue) -> {
                rect.setVisible(newValue);
                godInfoBox.setVisible(newValue);
            });

            if (shouldPick) {
                //Set click functionality on the images
                godStack.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        if (chosenGods.size() < howMany && !chosenGods.contains(god)) {
                            //Left click to select
                            Glow glow = new Glow(0.7);
                            imageView.setEffect(glow);
                            chosenGods.add(god);
                        }
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        //Right click to deselect
                        imageView.setEffect(null);
                        chosenGods.remove(god);
                    }

                    selectGodsBtn.setDisable(chosenGods.size() != howMany);
                });
            }

            godsPlayable.getChildren().add(godStack);
        }

        selectGodsBtn.setOnAction(e -> {
            if (chosenGods.size() == howMany) {
                selectAction.choose(chosenGods);
            }
        });
        this.<Node>lookup(GOD_SELECTION_VIEW).setVisible(true);
    }

    /**
     * End the god selection game phase.
     * Enter playing phase.
     */
    public void endGodSelectionPhase() {
        this.<Node>lookup(GOD_SELECTION_VIEW).setVisible(false);
        this.<Node>lookup(TRANSPARENCY).setVisible(false);
        this.<Node>lookup(BOARD).setVisible(true);
        this.<Node>lookup(GAME_LABEL).setVisible(true);
    }

    /**
     * Update the players and spectators list
     * @param players the list of players
     * @param spectators the list of spectators
     * @param currentP the current player
     */
    public void updatePlayers(List<Player> players, List<Player> spectators, Player currentP) {
        VBox onlinePlayersBox = this.lookup(PLAYER_LIST);
        onlinePlayersBox.getChildren().clear();

        Label playersHeader = new Label("Players:");
        FXUtils.embellishLabel(playersHeader, Color.BLACK, 20);
        onlinePlayersBox.getChildren().add(playersHeader);
        for(Player p : players) {
            HBox hBox = new HBox(2);
            if(p.equals(currentP)) {
                Label turnIndicator = new Label("->");
                FXUtils.embellishLabel(turnIndicator, colors.get(p), 15);
                hBox.getChildren().add(turnIndicator);
            }
            Label label = new Label(p.getName());
            label.setWrapText(true);
            FXUtils.embellishLabel(label, colors.get(p), 15);
            hBox.getChildren().add(label);
            onlinePlayersBox.getChildren().add(hBox);
        }

        Label spectatorsHeader = new Label("Spectators:");
        FXUtils.embellishLabel(spectatorsHeader, Color.BLACK, 20);
        onlinePlayersBox.getChildren().add(spectatorsHeader);
        for(Player s : spectators) {
            Label label = new Label(s.getName());
            label.setWrapText(true);
            FXUtils.embellishLabel(label, Color.BLACK, 15);
            onlinePlayersBox.getChildren().add(label);
        }
    }

    /**
     * Populate the board ui element with listeners, and render it based on its size
     * @param dimX the x-axis board size
     * @param dimY the y-axis board size
     * @param clickListener the callback after clicking on a tile
     */
    public void createBoard(int dimX, int dimY, BoardClickListener clickListener) {
        GridPane boardGrid = lookup(GameplayScene.BOARD);
        double tileMargin = getTileMargin();
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                StackPane tileStack = new StackPane();
                tileStack.setMinSize(getTileSize(), getTileSize());
                tileStack.setMaxSize(getTileSize(), getTileSize());
                tileStack.setId(i + "" + j);
                GridPane.setRowIndex(tileStack, i);
                GridPane.setColumnIndex(tileStack, j);
                tileStack.setOnMouseClicked(e -> clickListener.handle(GridPane.getRowIndex(tileStack), GridPane.getColumnIndex(tileStack)));

                if (dimX != 5 || dimY != 5) {
                    Rectangle rect = new Rectangle(getTileSize(), getTileSize());
                    rect.setStroke(Color.WHITE);
                    rect.setStrokeWidth(getTileSize() / 12);
                    rect.setFill(Color.TRANSPARENT);
                    rect.setId(SET_ID(RECT_PREFIX + i + "" + j)); // make unique ids but recognizable by a common prefix
                    tileStack.getChildren().add(rect);
                }

                boardGrid.setMaxWidth(dimX * (getTileSize() + tileMargin));
                boardGrid.setMaxHeight(dimX * (getTileSize() + tileMargin));
                boardGrid.getChildren().add(tileStack);
            }
        }

        if (dimX != 5 || dimY != 5) {
            StackPane mainStack = lookup(MAIN_STACK);
            mainStack.getChildren().set(0, new Rectangle(width, height, Color.AQUAMARINE));
        }
    }

    /**
     * Populate the storage ui element based on the storage size
     * @param store the storage
     */
    public void createStorage(Storage store) {
        VBox container = lookup(GameplayScene.STORAGE);
        for (int i = 0; i < store.getPieceTypes(); i++) {
            HBox hbox = new HBox(2);
            Label lvlamt = new Label("x " + store.getAvailable(i));
            FXUtils.embellishLabel(lvlamt, Color.BLACK, 40);
            lvlamt.setId(SET_ID(LEVEL_LABEL_PREFIX + i));
            Node lvl;
            if (i < levels.size()) {
                lvl = new ImageView(levels.get(i));
            } else if (i != store.getPieceTypes() - 1) {
                Label lvlAsLabel = new Label(i + "");
                FXUtils.embellishLabel(lvlAsLabel, Color.BLACK, 95);
                lvl = lvlAsLabel;
            } else {
                lvl = new ImageView(domeImage);
            }
            hbox.getChildren().add(lvl);
            hbox.getChildren().add(lvlamt);
            container.getChildren().add(hbox);
        }
    }

    /**
     * Update the storage availability
     * @param store
     */
    public void updateStorage(Storage store) {
        for (int i = 0; i < store.getPieceTypes(); i++) {
            Label label = lookup(LEVEL_LABEL_PREFIX + i);
            label.setText("x " + store.getAvailable(i));
            FXUtils.embellishLabel(label, Color.BLACK, 40);
        }
    }

    // Draw tile
    private final Image lvl0 = new Image("Level0.png", getTileSize(), getTileSize(), true, true);
    private final Image lvl1 = new Image("Level1.png", getTileSize(), getTileSize(), true, true);
    private final Image lvl2 = new Image("Level2.png", getTileSize(), getTileSize(), true, true);
    private final Image domeImage = new Image("Dome.png", getTileSize(), getTileSize(), true, true);
    private final Image workerImage = new Image("Worker.png", getTileSize(), getTileSize(), true, true);
    private final ArrayList<Image> levels = new ArrayList<>(Arrays.asList(lvl0, lvl1, lvl2));

    /**
     * Draw a tile into a pane
     * @param stackPane the target pane
     * @param tile the tile to be drawn
     */
    public void drawTileInto(StackPane stackPane, Tile tile) {
        stackPane.getChildren().removeIf(node -> node.getId() == null || !node.getId().startsWith(SET_ID(RECT_PREFIX)));

        for (int i = 0; i < tile.getHeight() && i < levels.size(); i++) {
            Node levelNode = new ImageView(levels.get(i));
            stackPane.getChildren().add(levelNode);
        }
        if (tile.getHeight() > levels.size()) {
            Label heightLabel = new Label();
            heightLabel.setText(tile.getHeight() + "");
            FXUtils.embellishLabel(heightLabel, Color.BLACK, 70);
            stackPane.getChildren().add(heightLabel);
        }
        if (tile.hasDome()) {
            stackPane.getChildren().add(new ImageView(domeImage));
        } else if (tile.getOccupant() != null) {
            Color color = colors.get(tile.getOccupant().getOwner());
            Image coloredWorker = color == null ? workerImage :
                    colorWorkers(workerImage, color, tile.getHeight() > levels.size());
            ImageView worker = new ImageView(coloredWorker);
            stackPane.getChildren().add(worker);
        }
    }

    public double getTileSize() {
        return height / 7.5d;
    }

    public double getTileMargin() {
        return height / 69;
    }

    /**
     * Edit a worker image to have a certain color and/or be semitransparent
     * @param workerToColor the original image
     * @param color the new color
     * @param semitransparent should it be semitransparent
     * @return the edited image
     */
    private Image colorWorkers(Image workerToColor, Color color, boolean semitransparent) {
        if (semitransparent) {
            color = Color.rgb(
                    (int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255),
                    (int)(color.getBlue() * 255),
                    0.8);
        }

        PixelReader reader = workerToColor.getPixelReader();

        int w = (int) workerToColor.getWidth();
        int h = (int) workerToColor.getHeight();

        WritableImage coloredWorker = new WritableImage(w, h);
        PixelWriter writer = coloredWorker.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color imgColor = reader.getColor(i, j);
                if (!imgColor.equals(Color.TRANSPARENT)) {
                    writer.setColor(i, j, color);
                }
            }
        }
        return coloredWorker;
    }

    /**
     * Get a god image from resources
     * @param godName the god name
     * @return the god image as a stream
     */
    public InputStream getGodImageResourceFor(String godName) {
        InputStream res = getClass().getResourceAsStream("/godcards/" + godName + ".png");
        if (res == null) {
            res = getClass().getResourceAsStream("/godcards/CustomGod.png");
        }
        return res;
    }
}
