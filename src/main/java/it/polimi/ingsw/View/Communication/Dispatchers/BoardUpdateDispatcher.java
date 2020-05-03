package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.BoardUpdateMessage;
import it.polimi.ingsw.View.Communication.Listeners.BoardUpdateListener;

public interface BoardUpdateDispatcher {
    void addBoardUpdateListener(BoardUpdateListener listener);
    void removeBoardUpdateListener(BoardUpdateListener listener);
    void notifyBoardUpdate(BoardUpdateMessage message);

    void onRegisterForBoardUpdate(BoardUpdateListener listener);
}
