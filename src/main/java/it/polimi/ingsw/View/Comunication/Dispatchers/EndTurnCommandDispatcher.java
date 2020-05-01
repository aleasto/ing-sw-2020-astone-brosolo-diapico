package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.EndTurnCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.EndTurnCommandListener;

import java.util.ArrayList;
import java.util.List;

public interface EndTurnCommandDispatcher {
    List<EndTurnCommandListener> listeners = new ArrayList<>();

    default void addEndTurnCommandListener(EndTurnCommandListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    default void removeEndTurnCommandListener(EndTurnCommandListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    
    default void notifyEndTurnCommand(EndTurnCommandMessage command) {
        synchronized (listeners) {
            for (EndTurnCommandListener listener : listeners) {
                listener.onEndTurnCommand(command);
            }
        }
    }
}
