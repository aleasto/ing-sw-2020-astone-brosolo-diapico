package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.Listeners.PlayerLoseEventListener;
import it.polimi.ingsw.View.Communication.PlayerLoseEventMessage;

public interface PlayerLoseEventBroadcaster {
    void addPlayerLoseEventListener(PlayerLoseEventListener listener);
    void removePlayerLoseEventListener(PlayerLoseEventListener listener);
    void notifyPlayerLoseEvent(PlayerLoseEventMessage message);
}
