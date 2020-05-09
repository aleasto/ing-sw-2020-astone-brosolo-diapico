package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.PlayerTurnUpdateMessage;
import it.polimi.ingsw.View.Communication.Listeners.PlayerTurnUpdateListener;

public interface PlayerTurnUpdateDispatcher {
    void addPlayerTurnUpdateListener(PlayerTurnUpdateListener listener);
    void removePlayerTurnUpdateListener(PlayerTurnUpdateListener listener);
    void notifyPlayerTurnUpdate(PlayerTurnUpdateMessage message);

    void onRegisterForPlayerTurnUpdate(PlayerTurnUpdateListener listener);
}
