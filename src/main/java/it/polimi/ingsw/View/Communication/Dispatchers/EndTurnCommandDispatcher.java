package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.EndTurnCommandMessage;
import it.polimi.ingsw.View.Communication.Listeners.EndTurnCommandListener;

public interface EndTurnCommandDispatcher {
    void addEndTurnCommandListener(EndTurnCommandListener listener);
    void removeEndTurnCommandListener(EndTurnCommandListener listener);
    void notifyEndTurnCommand(EndTurnCommandMessage command);
}
