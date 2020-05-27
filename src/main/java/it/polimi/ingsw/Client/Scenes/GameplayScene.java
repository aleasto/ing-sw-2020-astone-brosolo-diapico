package it.polimi.ingsw.Client.Scenes;

import it.polimi.ingsw.Client.BoardClickListener;
import it.polimi.ingsw.Client.GodSelectionListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double width = screenSize.getWidth();
    private final double height = screenSize.getHeight();
    private BoardClickListener boardClickListener = null;

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

        // Online players
        VBox playerListContainer = new VBox(10);
        playerListContainer.setId(SET_ID(PLAYER_LIST));
        playerListContainer.setAlignment(Pos.CENTER);
        Rectangle onlinePlayersBG = new Rectangle(250, height / 2, Color.ORANGE);
        AnchorPane playersAnchorPane = new AnchorPane(onlinePlayersBG, playerListContainer);
        AnchorPane.setTopAnchor(playerListContainer, 15d);
        AnchorPane.setRightAnchor(playerListContainer, 120d);
        AnchorPane.setTopAnchor(onlinePlayersBG, 1d);
        AnchorPane.setRightAnchor(onlinePlayersBG, 1d);
        playersAnchorPane.setId(SET_ID(PLAYER_LIST_PANE));
        stack.getChildren().add(playersAnchorPane);

        // Start button
        Button startTheGame = new Button("Start!");
        startTheGame.setVisible(false); // only the host will show it
        startTheGame.setId(SET_ID(START_BTN));
        stack.getChildren().add(startTheGame);

        // God selection
        Button selectGodsBtn = new Button("Select");
        selectGodsBtn.setDisable(true);
        selectGodsBtn.setId(SET_ID(SELECT_GODS_BTN));
        HBox godsPlayable = new HBox(20);
        godsPlayable.setAlignment(Pos.CENTER);
        godsPlayable.setId(SET_ID(GOD_LIST));
        VBox godSelectionView = new VBox(2);
        godSelectionView.getChildren().addAll(godsPlayable, selectGodsBtn);
        godSelectionView.setAlignment(Pos.CENTER);
        godSelectionView.setSpacing(45);
        godSelectionView.setId(SET_ID(GOD_SELECTION_VIEW));
        godSelectionView.setVisible(false);
        stack.getChildren().add(godSelectionView);

        // Board grid
        GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setPadding(new Insets(height/60, 0, 0, 0));
        boardGrid.setHgap(height/69);
        boardGrid.setVgap(height/69);
        //TODO: Make dimensions settable maybe
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Rectangle rectangle = new Rectangle(height/7.5d, height/7.5d);
                rectangle.setFill(Color.BLACK);
                GridPane.setRowIndex(rectangle, i);
                GridPane.setColumnIndex(rectangle, j);
                boardGrid.getChildren().add(rectangle);
            }
        }
        boardGrid.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (boardClickListener != null) {
                for (Node node : boardGrid.getChildren()) {
                    if (node instanceof Rectangle) {
                        if (node.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                            boardClickListener.handle(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
                        }
                    }
                }
            }
        });
        boardGrid.setId(SET_ID(BOARD));
        boardGrid.setVisible(false);
        stack.getChildren().add(boardGrid);

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
            ImageView imageView = new ImageView(new Image("/godcards/" + god + ".png", width / 15, height * 0.4, true, true));

            if (shouldPick) {
                //Set click functionality on the images
                imageView.setPickOnBounds(true);
                imageView.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        if(chosenGods.size() < howMany && !chosenGods.contains(god)) {
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

    @Override
    public Scene getFXScene() {
        return scene;
    }
}
