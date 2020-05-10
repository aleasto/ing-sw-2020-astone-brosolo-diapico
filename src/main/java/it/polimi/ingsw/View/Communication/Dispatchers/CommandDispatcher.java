package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.CommandMessage;
import it.polimi.ingsw.View.Communication.Listeners.CommandListener;

public interface CommandDispatcher {
    void addCommandListener(CommandListener listener);
    void removeCommandListener(CommandListener listener);
    void notifyCommand(CommandMessage message);
}
