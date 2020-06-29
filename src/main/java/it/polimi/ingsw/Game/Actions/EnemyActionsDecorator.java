package it.polimi.ingsw.Game.Actions;

public class EnemyActionsDecorator extends ActionsDecorator {
    private final Actions enemy;

    public EnemyActionsDecorator(Actions decorated, Actions enemy) {
        super(decorated);
        this.enemy = enemy;
    }

    public Actions getEnemy() {
        return enemy;
    }
}
