package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.Listeners.PlayerChoseGodEventListener;
import it.polimi.ingsw.View.Communication.PlayerChoseGodEventMessage;

public interface PlayerChoseGodEventBroadcaster {
    void addPlayerChoseGodEventListener(PlayerChoseGodEventListener listener);
    void removePlayerChoseGodEventListener(PlayerChoseGodEventListener listener);
    void notifyPlayerChoseGodEvent(PlayerChoseGodEventMessage message);
}
