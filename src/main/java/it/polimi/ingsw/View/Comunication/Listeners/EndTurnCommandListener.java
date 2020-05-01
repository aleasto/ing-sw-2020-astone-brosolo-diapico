package it.polimi.ingsw.View.Comunication.Listeners;

import it.polimi.ingsw.View.Comunication.EndTurnCommandMessage;

public interface EndTurnCommandListener {
    void onEndTurnCommand(EndTurnCommandMessage message);
}
