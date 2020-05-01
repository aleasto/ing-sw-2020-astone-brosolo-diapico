package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.Listeners.TextListener;
import it.polimi.ingsw.View.Comunication.StorageUpdateMessage;
import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;

import java.util.ArrayList;
import java.util.List;

public interface StorageUpdateDispatcher {
    List<StorageUpdateListener> listeners = new ArrayList<>();

    default void addStorageUpdateListener(StorageUpdateListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
        onRegisterForStorageUpdate(listener);
    }

    default void removeStorageUpdateListener(StorageUpdateListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    default void notifyStorageUpdate(StorageUpdateMessage message) {
        synchronized (listeners) {
            for (StorageUpdateListener listener : listeners) {
                listener.onStorageUpdate(message);
            }
        }
    }

    void onRegisterForStorageUpdate(StorageUpdateListener listener);
}
