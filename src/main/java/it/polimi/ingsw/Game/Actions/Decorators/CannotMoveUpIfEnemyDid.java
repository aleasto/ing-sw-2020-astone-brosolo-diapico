package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Game.Worker;

public class CannotMoveUpIfEnemyDid extends ActionsDecorator {
    private final Actions enemy; // This will basically hold a reference to Athena, the enemy god that imposed us this malus

    public CannotMoveUpIfEnemyDid(Actions decorated, Actions enemy) {
        super(decorated);
        this.enemy = enemy;
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        if (enemy.hasLost()) {
            // This effect is no longer relevant as the player which caused it is no longer playing
            return super.validMove(w, to);
        }

        Pair<Tile, Tile> enemyLastMove = enemy.getLastMove();
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
