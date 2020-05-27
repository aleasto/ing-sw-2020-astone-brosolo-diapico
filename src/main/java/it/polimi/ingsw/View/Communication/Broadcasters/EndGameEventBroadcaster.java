package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.Listeners.EndGameEventListener;
import it.polimi.ingsw.View.Communication.EndGameEventMessage;

public interface EndGameEventBroadcaster {
    void addEndGameEventListener(EndGameEventListener listener);
    void removeEndGameEventListener(EndGameEventListener listener);
    void notifyEndGameEvent(EndGameEventMessage message);
}
