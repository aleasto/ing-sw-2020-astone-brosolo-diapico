package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.MoveCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.MoveCommandListener;

public interface MoveCommandDispatcher {
    void addMoveCommandListener(MoveCommandListener listener);
    void removeMoveCommandListener(MoveCommandListener listener);
    void notifyMoveCommand(MoveCommandMessage command);
}
