package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.ActionsDecorator;

public class CannotMoveUpIfEnemyDid extends ActionsDecorator {
    public CannotMoveUpIfEnemyDid(Actions decorated, Actions enemy) {
        super(decorated);
    }
}
