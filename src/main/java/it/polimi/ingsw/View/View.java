package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.BuildCommandMessage;
import it.polimi.ingsw.View.Communication.Dispatchers.BuildCommandDispatcher;
import it.polimi.ingsw.View.Communication.Dispatchers.EndTurnCommandDispatcher;
import it.polimi.ingsw.View.Communication.Dispatchers.MoveCommandDispatcher;
import it.polimi.ingsw.View.Communication.EndTurnCommandMessage;
import it.polimi.ingsw.View.Communication.Listeners.*;
import it.polimi.ingsw.View.Communication.MoveCommandMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class View implements
        BoardUpdateListener, StorageUpdateListener, NextActionsUpdateListener, TextListener, // listeners
        MoveCommandDispatcher, BuildCommandDispatcher, EndTurnCommandDispatcher {
    protected Player me;

    public View(Player me) {
        this.me = me;
    }

    public Player getPlayer() {
        return me;
    }

    //<editor-fold desc="Listener registration handling">
    final List<MoveCommandListener> moveCommandListeners = new ArrayList<>();
    @Override
    public void addMoveCommandListener(MoveCommandListener listener){
        synchronized (moveCommandListeners) {
            moveCommandListeners.add(listener);
        }
    }
    @Override
    public void removeMoveCommandListener(MoveCommandListener listener){
        synchronized (moveCommandListeners) {
            moveCommandListeners.remove(listener);
        }
    }
    @Override
    public void notifyMoveCommand(MoveCommandMessage command) {
        synchronized (moveCommandListeners) {
            for (MoveCommandListener listener : moveCommandListeners) {
                listener.onMoveCommand(command);
            }
        }
    }

    final List<BuildCommandListener> buildCommandListeners = new ArrayList<>();
    @Override
    public void addBuildCommandListener(BuildCommandListener listener){
        synchronized (buildCommandListeners) {
            buildCommandListeners.add(listener);
        }
    }
    @Override
    public void removeBuildCommandListener(BuildCommandListener listener){
        synchronized (buildCommandListeners) {
            buildCommandListeners.remove(listener);
        }
    }
    @Override
    public void notifyBuildCommand(BuildCommandMessage command) {
        synchronized (buildCommandListeners) {
            for (BuildCommandListener listener : buildCommandListeners) {
                listener.onBuildCommand(command);
            }
        }
    }

    final List<EndTurnCommandListener> endTurnCommandListeners = new ArrayList<>();
    @Override
    public void addEndTurnCommandListener(EndTurnCommandListener listener){
        synchronized (endTurnCommandListeners) {
            endTurnCommandListeners.add(listener);
        }
    }
    @Override
    public void removeEndTurnCommandListener(EndTurnCommandListener listener){
        synchronized (endTurnCommandListeners) {
            endTurnCommandListeners.remove(listener);
        }
    }
    @Override
    public void notifyEndTurnCommand(EndTurnCommandMessage command) {
        synchronized (endTurnCommandListeners) {
            for (EndTurnCommandListener listener : endTurnCommandListeners) {
                listener.onEndTurnCommand(command);
            }
        }
    }
    //</editor-fold>
}
