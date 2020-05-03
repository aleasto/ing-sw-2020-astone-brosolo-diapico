package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.BuildCommandMessage;

public interface BuildCommandListener {
    void onBuildCommand(BuildCommandMessage message);
}
