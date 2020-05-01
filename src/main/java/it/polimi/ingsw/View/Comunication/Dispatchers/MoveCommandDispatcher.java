package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.MoveCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.MoveCommandListener;

import java.util.ArrayList;
import java.util.List;

public interface MoveCommandDispatcher {
    List<MoveCommandListener> listeners = new ArrayList<>();

    default void addMoveCommandListener(MoveCommandListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    default void removeMoveCommandListener(MoveCommandListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    default void notifyMoveCommand(MoveCommandMessage command) {
        synchronized (listeners) {
            for (MoveCommandListener listener : listeners) {
                listener.onMoveCommand(command);
            }
        }
    }
}
