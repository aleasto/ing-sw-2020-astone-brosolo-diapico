package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.BoardUpdateMessage;
import it.polimi.ingsw.View.Communication.Listeners.BoardUpdateListener;

public interface BoardUpdateBroadcaster {
    void addBoardUpdateListener(BoardUpdateListener listener);
    void removeBoardUpdateListener(BoardUpdateListener listener);
    void notifyBoardUpdate(BoardUpdateMessage message);

    void onRegisterForBoardUpdate(BoardUpdateListener listener);
}
