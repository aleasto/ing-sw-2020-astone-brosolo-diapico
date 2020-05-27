package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.EndGameEventMessage;

public interface EndGameEventListener {
    void onEndGameEvent(EndGameEventMessage message);
}
