package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Comunication.Dispatchers.BuildCommandDispatcher;
import it.polimi.ingsw.View.Comunication.Dispatchers.EndTurnCommandDispatcher;
import it.polimi.ingsw.View.Comunication.Dispatchers.MoveCommandDispatcher;
import it.polimi.ingsw.View.Comunication.Listeners.BoardUpdateListener;
import it.polimi.ingsw.View.Comunication.Listeners.NextActionsUpdateListener;
import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;
import it.polimi.ingsw.View.Comunication.Listeners.TextListener;

public abstract class View implements
        BoardUpdateListener, StorageUpdateListener, NextActionsUpdateListener, TextListener, // listeners
        MoveCommandDispatcher, BuildCommandDispatcher, EndTurnCommandDispatcher {
    protected Player me;

    public View(Player me) {
        this.me = me;
    }
}
