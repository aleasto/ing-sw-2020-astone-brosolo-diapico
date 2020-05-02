package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.BuildCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.BuildCommandListener;

public interface BuildCommandDispatcher {
    void addBuildCommandListener(BuildCommandListener listener);
    void removeBuildCommandListener(BuildCommandListener listener);
    void notifyBuildCommand(BuildCommandMessage command);
}
