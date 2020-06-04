package it.polimi.ingsw.View;

import it.polimi.ingsw.View.Communication.CommandMessage;

public interface CommandListener {
    void handle(CommandMessage message);
}
