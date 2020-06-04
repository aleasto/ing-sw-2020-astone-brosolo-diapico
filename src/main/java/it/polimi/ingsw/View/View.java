package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Listeners.*;

public abstract class View implements
        BoardUpdateListener, StorageUpdateListener, NextActionsUpdateListener, TextListener,
        PlayersUpdateListener, ShowGodsListener, PlayerTurnUpdateListener, PlayerLoseEventListener,
        EndGameEventListener, PlayerChoseGodEventListener, LobbiesUpdateListener {
    protected Player me;

    public View(Player me) {
        this.me = me;
    }

    public Player getPlayer() {
        return me;
    }

    public abstract void onCommand(CommandMessage message);
}
