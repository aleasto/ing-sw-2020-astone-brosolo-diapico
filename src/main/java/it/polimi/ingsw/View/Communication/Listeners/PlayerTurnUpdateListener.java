package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.PlayerTurnUpdateMessage;

public interface PlayerTurnUpdateListener {
    void onPlayerTurnUpdate(PlayerTurnUpdateMessage message);
}
