package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.BuildCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.BuildCommandListener;

import java.util.ArrayList;
import java.util.List;

public interface BuildCommandDispatcher {
    List<BuildCommandListener> listeners = new ArrayList<>();

    default void addBuildCommandListener(BuildCommandListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    default void removeBuildCommandListener(BuildCommandListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    
    default void notifyBuildCommand(BuildCommandMessage command) {
        synchronized (listeners) {
            for (BuildCommandListener listener : listeners) {
                listener.onBuildCommand(command);
            }
        }
    }
}
