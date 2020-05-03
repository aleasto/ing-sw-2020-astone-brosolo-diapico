package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.BoardUpdateMessage;

public interface BoardUpdateListener {
    void onBoardUpdate(BoardUpdateMessage message);
}
