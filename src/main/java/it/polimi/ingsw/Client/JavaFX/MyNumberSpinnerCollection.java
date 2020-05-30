package it.polimi.ingsw.Client.JavaFX;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

public class MyNumberSpinnerCollection extends HBox {

    public MyNumberSpinnerCollection(Integer[] initialNumbers) {
        super(10);
        this.setAlignment(Pos.CENTER);
        Button removeBtn = new Button("-");
        removeBtn.setOnAction(e -> {
            e.consume();

            ObservableList<Node> spinners = getSpinners();
            if (spinners.size() <= 1)
                return;
            getChildren().remove(getChildren().size() - 2);
        });

        Button addBtn = new Button("+");
        addBtn.setOnAction(e -> {
            e.consume();

            Integer nextInitialValue = ((Spinner<Integer>)getSpinners().get(getSpinners().size() - 1)).getValue();
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

    public ObservableList<Node> getSpinners() {
        return getChildren().filtered(node -> node instanceof Spinner);
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
