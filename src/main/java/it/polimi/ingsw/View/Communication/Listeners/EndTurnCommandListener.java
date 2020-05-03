package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.EndTurnCommandMessage;

public interface EndTurnCommandListener {
    void onEndTurnCommand(EndTurnCommandMessage message);
}
