package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.StartGameCommandMessage;

public interface StartGameCommandListener {
    void onStartGame(StartGameCommandMessage message);
}
