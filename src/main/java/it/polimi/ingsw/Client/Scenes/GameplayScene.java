package it.polimi.ingsw.Client.Scenes;

import it.polimi.ingsw.Client.BoardClickListener;
import it.polimi.ingsw.Client.GodSelectionListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameplayScene extends SantoriniScene {

    public static final String START_BTN = "#start_btn";
    public static final String PLAYER_LIST = "#player_list";
    public static final String PLAYER_LIST_PANE = "#player_list_pane";
    public static final String SELECT_GODS_BTN = "#select_gods_btn";
    public static final String GOD_LIST = "#god_list";
    public static final String GOD_SELECTION_VIEW = "#god_selection_view";
    public static final String TRANSPARENCY = "#transparency";
    public static final String BOARD = "#board";
    public static final String GOD_SELECTION_LABEL = "#god_selection_label";
    public static final String GAME_LABEL = "#game_label";
    public static final String MY_GOD = "#my_god";
    public static final String END_TURN_BTN = "#end_turn_btn";

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double width = screenSize.getWidth();
    private final double height = screenSize.getHeight();
    private BoardClickListener boardClickListener = null;
    private EventHandler<MouseEvent> boardHandler;

    private final Scene scene;

    public GameplayScene() {
        Image background = new Image("SantoriniBoard.png", width, height, true, true);

        // Stack up different panes (background, board, godpool, etc...)
        StackPane stack = new StackPane(new ImageView(background));

        // Transparency
        Rectangle transparency = new Rectangle(width, height);
        transparency.setFill(Color.rgb(0, 0, 0, 0.90));
        transparency.setId(SET_ID(TRANSPARENCY));
        stack.getChildren().add(transparency);

        // Anchor pane for Players, God card, Game guide label, EndTurn Button
        VBox playerListContainer = new VBox(10);
        playerListContainer.setId(SET_ID(PLAYER_LIST));
        playerListContainer.setAlignment(Pos.TOP_LEFT);

        ImageView myGod = new ImageView();
        myGod.setVisible(false);
        myGod.setFitHeight(height / 2);
        myGod.setFitWidth(width / 6.4);
        myGod.setId(SET_ID(MY_GOD));

        Label gameGuide = new Label("");
        gameGuide.setId(SET_ID(GAME_LABEL));
        gameGuide.setVisible(false);
        gameGuide.setMaxWidth(Double.MAX_VALUE);
        gameGuide.setAlignment(Pos.CENTER);
        embellishLabel(gameGuide, Color.BLACK, 15);

        Rectangle onlinePlayersBG = new Rectangle(width / 6.4, height / 2, Color.ORANGE);

        AnchorPane mainAnchorPane = new AnchorPane(onlinePlayersBG, playerListContainer, myGod, gameGuide);

        AnchorPane.setTopAnchor(onlinePlayersBG, 1d);
        AnchorPane.setRightAnchor(onlinePlayersBG, 1d);

        AnchorPane.setTopAnchor(playerListContainer, 15d);
        AnchorPane.setRightAnchor(playerListContainer, width / 8);

        AnchorPane.setBottomAnchor(myGod, 1d);
        AnchorPane.setRightAnchor(myGod, 1d);

        AnchorPane.setBottomAnchor(gameGuide, 15d);
        AnchorPane.setLeftAnchor(gameGuide, 0d);
        AnchorPane.setRightAnchor(gameGuide, 0d);

        mainAnchorPane.setId(SET_ID(PLAYER_LIST_PANE));
        stack.getChildren().add(mainAnchorPane);

        // Start button
        Button startTheGame = new Button("Start!");
        startTheGame.setVisible(false); // only the host will show it
        startTheGame.setId(SET_ID(START_BTN));
        stack.getChildren().add(startTheGame);

        // God selection
        Label godSelectionTip = new Label("Tip: Left Click to Select, Right Click to Deselect");
        embellishLabel(godSelectionTip, Color.WHITE, 25);
        Label godSelectionGuide = new Label("");
        godSelectionGuide.setId(SET_ID(GOD_SELECTION_LABEL));
        embellishLabel(godSelectionGuide, Color.WHITE, 35);
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
        boardGrid.setHgap(height / 69);
        boardGrid.setVgap(height / 69);
        //TODO: Make dimensions settable maybe
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane gridStack = new StackPane();
                Rectangle rectangle = new Rectangle(height / 7.5d, height / 7.5d);
                rectangle.setFill(Color.TRANSPARENT);
                gridStack.getChildren().add(rectangle);
                gridStack.setId(i + "" + j);
                GridPane.setRowIndex(gridStack, i);
                GridPane.setColumnIndex(gridStack, j);
                boardGrid.getChildren().add(gridStack);
            }
        }

        boardHandler = mouseEvent -> {
            if (boardClickListener != null) {
                for (Node node : boardGrid.getChildren()) {
                    if (node.getBoundsInParent().contains(mouseEvent.getSceneX(), mouseEvent.getSceneY())) {
                        boardClickListener.handle(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
                    }
                }
            }
        };

        boardGrid.addEventFilter(MouseEvent.MOUSE_PRESSED, boardHandler);
        boardGrid.setId(SET_ID(BOARD));
        boardGrid.setVisible(false);
        stack.getChildren().add(boardGrid);

        Button endTurn = new Button("End Turn");
        endTurn.setVisible(false);
        endTurn.setId(SET_ID(END_TURN_BTN));
        endTurn.setPrefSize(height / 7.5d, height / 7.5d);
        StackPane.setAlignment(endTurn, Pos.CENTER_LEFT);
        stack.getChildren().add(endTurn);

        this.scene = new Scene(stack, width, height);
    }

    public void setBoardClickListener(BoardClickListener listener) {
        boardClickListener = listener;
    }

    public void showAndPickGods(List<String> gods, boolean shouldPick, int howMany, GodSelectionListener selectAction) {
        HBox godsPlayable = lookup(GOD_LIST);
        Button selectGodsBtn = lookup(SELECT_GODS_BTN);
        List<String> chosenGods = new ArrayList<>();

        selectGodsBtn.setVisible(shouldPick);
        selectGodsBtn.setDisable(true);

        godsPlayable.getChildren().clear();
        for (String god : gods) {
            //Create the god image
            Image image = new Image("/godcards/" + god + ".png", width / 15, height * 0.4, true, true);
            ImageView imageView = new ImageView(image);

            if (shouldPick) {
                //Set click functionality on the images
                imageView.setPickOnBounds(true);
                imageView.setOnMouseClicked(e -> {
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
            godsPlayable.getChildren().add(imageView);
        }

        selectGodsBtn.setOnAction(e -> {
            if (chosenGods.size() == howMany) {
                selectAction.choose(chosenGods);
            }
        });
        this.<Node>lookup(GOD_SELECTION_VIEW).setVisible(true);
    }

    public void endGodSelectionPhase() {
        this.<Node>lookup(GOD_SELECTION_VIEW).setVisible(false);
        this.<Node>lookup(TRANSPARENCY).setVisible(false);
        this.<Node>lookup(BOARD).setVisible(true);
        this.<Node>lookup(GAME_LABEL).setVisible(true);
        this.<Node>lookup(END_TURN_BTN).setVisible(true);
    }

    private static void embellishLabel(Label label, Color color, int boldness) {
        label.setTextFill(color);
        label.setFont(Font.font(label.getFont().toString(), FontWeight.BOLD, boldness));
    }

    @Override
    public Scene getFXScene() {
        return scene;
    }
}
