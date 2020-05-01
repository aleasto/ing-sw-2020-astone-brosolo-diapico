package it.polimi.ingsw.View.Comunication.Listeners;

import it.polimi.ingsw.View.Comunication.MoveCommandMessage;

public interface MoveCommandListener {
    void onMoveCommand(MoveCommandMessage message);
}
