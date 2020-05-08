package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.Listeners.StartGameCommandListener;
import it.polimi.ingsw.View.Communication.StartGameCommandMessage;

public interface StartGameCommandDispatcher {
    void addStartGameCommandListener(StartGameCommandListener listener);
    void removeStartGameCommandListener(StartGameCommandListener listener);
    void notifyStartGameCommand(StartGameCommandMessage command);
}
