package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.BuildCommandMessage;
import it.polimi.ingsw.View.Communication.Listeners.BuildCommandListener;

public interface BuildCommandDispatcher {
    void addBuildCommandListener(BuildCommandListener listener);
    void removeBuildCommandListener(BuildCommandListener listener);
    void notifyBuildCommand(BuildCommandMessage command);
}
