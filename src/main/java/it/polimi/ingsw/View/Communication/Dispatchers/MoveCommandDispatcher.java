package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.MoveCommandMessage;
import it.polimi.ingsw.View.Communication.Listeners.MoveCommandListener;

public interface MoveCommandDispatcher {
    void addMoveCommandListener(MoveCommandListener listener);
    void removeMoveCommandListener(MoveCommandListener listener);
    void notifyMoveCommand(MoveCommandMessage command);
}
