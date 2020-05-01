package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.BoardUpdateMessage;
import it.polimi.ingsw.View.Comunication.Listeners.BoardUpdateListener;
import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;

import java.util.ArrayList;
import java.util.List;

public interface BoardUpdateDispatcher {
    List<BoardUpdateListener> listeners = new ArrayList<>();

    default void addBoardUpdateListener(BoardUpdateListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
        onRegisterForBoardUpdate(listener);
    }

    default void removeBoardUpdateListener(BoardUpdateListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    default void notifyBoardUpdate(BoardUpdateMessage message) {
        synchronized (listeners) {
            for (BoardUpdateListener listener : listeners) {
                listener.onBoardUpdate(message);
            }
        }
    }

    void onRegisterForBoardUpdate(BoardUpdateListener listener);
}
