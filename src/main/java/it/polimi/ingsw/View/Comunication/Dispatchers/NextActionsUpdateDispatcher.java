package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;
import it.polimi.ingsw.View.Comunication.NextActionsUpdateMessage;
import it.polimi.ingsw.View.Comunication.Listeners.NextActionsUpdateListener;

import java.util.ArrayList;
import java.util.List;

public interface NextActionsUpdateDispatcher {
    void addNextActionsUpdateListener(NextActionsUpdateListener listener);
    void removeNextActionsUpdateListener(NextActionsUpdateListener listener);
    void notifyNextActionsUpdate(NextActionsUpdateMessage message);

    void onRegisterForNextActionsUpdate(NextActionsUpdateListener listener);
}
