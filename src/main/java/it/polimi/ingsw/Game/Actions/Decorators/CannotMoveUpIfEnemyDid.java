package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Game.Worker;

public class CannotMoveUpIfEnemyDid extends ActionsDecorator {
    private Actions enemy; // This will basically hold a reference to Athena, the enemy god that imposed us this malus

    public CannotMoveUpIfEnemyDid(Actions decorated, Actions enemy) {
        super(decorated);
        this.enemy = enemy;
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        Pair<Tile> enemyLastMove = enemy.getLastMove();
        boolean hasEnemyMovedUp = (enemyLastMove != null &&
                                   enemyLastMove.getSecond().getHeight() > enemyLastMove.getFirst().getHeight());
        if(hasEnemyMovedUp) {
            // I can't move up
            if(to.getHeight() > w.getTile().getHeight())
                return false;
        }
        // Else we leave judgment to super
        return super.validMove(w, to);
    }
}
