package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.EndTurnCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.EndTurnCommandListener;

public interface EndTurnCommandDispatcher {
    void addEndTurnCommandListener(EndTurnCommandListener listener);
    void removeEndTurnCommandListener(EndTurnCommandListener listener);
    void notifyEndTurnCommand(EndTurnCommandMessage command);
}
