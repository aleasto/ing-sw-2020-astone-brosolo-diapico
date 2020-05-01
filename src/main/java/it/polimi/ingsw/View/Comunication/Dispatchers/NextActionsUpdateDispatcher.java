package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;
import it.polimi.ingsw.View.Comunication.NextActionsUpdateMessage;
import it.polimi.ingsw.View.Comunication.Listeners.NextActionsUpdateListener;

import java.util.ArrayList;
import java.util.List;

public interface NextActionsUpdateDispatcher {
    List<NextActionsUpdateListener> listeners = new ArrayList<>();

    default void addNextActionsUpdateListener(NextActionsUpdateListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
        onRegisterForNextActionsUpdate(listener);
    }

    default void removeNextActionsUpdateListener(NextActionsUpdateListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    default void notifyNextActionsUpdate(NextActionsUpdateMessage message) {
        synchronized (listeners) {
            for (NextActionsUpdateListener listener : listeners) {
                listener.onNextActionsUpdate(message);
            }
        }
    }

    void onRegisterForNextActionsUpdate(NextActionsUpdateListener listener);
}
