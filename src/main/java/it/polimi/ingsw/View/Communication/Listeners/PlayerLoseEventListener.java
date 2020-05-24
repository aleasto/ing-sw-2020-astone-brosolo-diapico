package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.PlayerLoseEventMessage;

public interface PlayerLoseEventListener {
    void onPlayerLoseEvent(PlayerLoseEventMessage message);
}
