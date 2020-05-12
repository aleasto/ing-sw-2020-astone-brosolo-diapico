package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Broadcasters.CommandBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.*;

import java.util.ArrayList;
import java.util.List;

public abstract class View implements
        BoardUpdateListener, StorageUpdateListener, NextActionsUpdateListener, TextListener,
        PlayersUpdateListener, ShowGodsListener, PlayerTurnUpdateListener,
        CommandBroadcaster {
    protected Player me;

    public View(Player me) {
        this.me = me;
    }

    public Player getPlayer() {
        return me;
    }

    //<editor-fold desc="Listener registration handling">
    final List<CommandListener> commandListeners = new ArrayList<>();
    @Override
    public void addCommandListener(CommandListener listener){
        synchronized (commandListeners) {
            commandListeners.add(listener);
        }
    }
    @Override
    public void removeCommandListener(CommandListener listener){
        synchronized (commandListeners) {
            commandListeners.remove(listener);
        }
    }
    @Override
    public void notifyCommand(CommandMessage command) {
        synchronized (commandListeners) {
            for (CommandListener listener : commandListeners) {
                listener.onCommand(command);
            }
        }
    }
    //</editor-fold>
}
