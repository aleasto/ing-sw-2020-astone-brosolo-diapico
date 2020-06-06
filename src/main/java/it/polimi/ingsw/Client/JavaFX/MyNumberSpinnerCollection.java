package it.polimi.ingsw.Client.JavaFX;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.stream.Collectors;

public class MyNumberSpinnerCollection extends HBox {

    public MyNumberSpinnerCollection(Integer[] initialNumbers) {
        super(10);
        this.setAlignment(Pos.CENTER);
        Button removeBtn = new Button("-");
        removeBtn.setOnAction(e -> {
            e.consume();

            if (getSpinners().size() <= 1)
                return;
            getChildren().remove(getChildren().size() - 2);
        });

        Button addBtn = new Button("+");
        addBtn.setOnAction(e -> {
            e.consume();

            Integer nextInitialValue = getSpinners().get(getSpinners().size() - 1).getValue();
            getChildren().add(getChildren().size() - 1, makeSpinner(nextInitialValue));
        });

        removeBtn.minWidthProperty().bind(addBtn.widthProperty());
        removeBtn.prefWidthProperty().bind(addBtn.widthProperty());
        removeBtn.maxWidthProperty().bind(addBtn.widthProperty());

        getChildren().add(removeBtn);
        for (Integer initialNumber : initialNumbers) {
            getChildren().add(makeSpinner(initialNumber));
        }
        getChildren().add(addBtn);
    }

    public List<Spinner<Integer>> getSpinners() {
        return getChildren().stream().filter(node -> node instanceof Spinner)
                .map(node -> (Spinner<Integer>) node).collect(Collectors.toList());
    }

    private Spinner<Integer> makeSpinner(Integer initialValue) {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setMaxWidth(60);
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, initialValue);

        spinner.setValueFactory(valueFactory);
        return spinner;
    }
}
