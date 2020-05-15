package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.LobbiesUpdateMessage;

public interface LobbiesUpdateListener {
    void onLobbiesUpdate(LobbiesUpdateMessage message);
}
