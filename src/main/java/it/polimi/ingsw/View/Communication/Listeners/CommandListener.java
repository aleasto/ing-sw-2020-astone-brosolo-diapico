package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.CommandMessage;

public interface CommandListener {
    void onCommand(CommandMessage message);
}
