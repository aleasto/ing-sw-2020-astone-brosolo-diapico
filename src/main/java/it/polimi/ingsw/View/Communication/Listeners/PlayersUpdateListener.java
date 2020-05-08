package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.PlayersUpdateMessage;

public interface PlayersUpdateListener {
    void onPlayersUpdate(PlayersUpdateMessage message);
}
