package it.polimi.ingsw.View.Comunication.Listeners;

import it.polimi.ingsw.View.Comunication.BoardUpdateMessage;

public interface BoardUpdateListener {
    void onBoardUpdate(BoardUpdateMessage message);
}
