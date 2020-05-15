package it.polimi.ingsw.View.Communication.Broadcasters;


import it.polimi.ingsw.View.Communication.Listeners.LobbiesUpdateListener;
import it.polimi.ingsw.View.Communication.LobbiesUpdateMessage;

public interface LobbiesUpdateBroadcaster {
    void addLobbiesUpdateListener(LobbiesUpdateListener listener);
    void removeLobbiesUpdateListener(LobbiesUpdateListener listener);
    void notifyLobbiesUpdate(LobbiesUpdateMessage message);

    void onRegisterForLobbiesUpdate(LobbiesUpdateListener listener);
}
