package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.BoardUpdateMessage;
import it.polimi.ingsw.View.Comunication.Listeners.BoardUpdateListener;

public interface BoardUpdateDispatcher {
    void addBoardUpdateListener(BoardUpdateListener listener);
    void removeBoardUpdateListener(BoardUpdateListener listener);
    void notifyBoardUpdate(BoardUpdateMessage message);

    void onRegisterForBoardUpdate(BoardUpdateListener listener);
}
