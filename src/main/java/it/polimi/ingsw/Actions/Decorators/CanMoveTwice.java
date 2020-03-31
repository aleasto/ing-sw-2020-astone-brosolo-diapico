package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.ActionsDecorator;

public class CanMoveTwice extends ActionsDecorator {
    public CanMoveTwice(Actions decorated) {
        super(decorated);
    }
}
