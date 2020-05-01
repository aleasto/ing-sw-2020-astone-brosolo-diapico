package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.View.Comunication.Listeners.TileUpdateListener;

import java.util.ArrayList;
import java.util.List;

public interface TileUpdateDispatcher {
    List<TileUpdateListener> listeners = new ArrayList<>();

    default void addTileUpdateListener(TileUpdateListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    default void removeTileUpdateListener(TileUpdateListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    default void notifyTileUpdate(Tile message) {
        synchronized (listeners) {
            for (TileUpdateListener listener : listeners) {
                listener.onTileUpdate(message);
            }
        }
    }
}
