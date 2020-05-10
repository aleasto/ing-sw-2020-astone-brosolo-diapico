package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.CommandMessage;
import it.polimi.ingsw.View.Communication.Listeners.CommandListener;

public interface CommandBroadcaster {
    void addCommandListener(CommandListener listener);
    void removeCommandListener(CommandListener listener);
    void notifyCommand(CommandMessage message);
}
