package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.ActionsDecorator;

public class CanSwapWithEnemy extends ActionsDecorator {
    public CanSwapWithEnemy(Actions decorated) {
        super(decorated);
    }
}
