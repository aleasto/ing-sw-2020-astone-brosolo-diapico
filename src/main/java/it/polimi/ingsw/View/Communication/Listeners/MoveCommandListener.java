package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.MoveCommandMessage;

public interface MoveCommandListener {
    void onMoveCommand(MoveCommandMessage message);
}
