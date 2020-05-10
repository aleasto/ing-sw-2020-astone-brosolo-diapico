package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.StorageUpdateMessage;
import it.polimi.ingsw.View.Communication.Listeners.StorageUpdateListener;

public interface StorageUpdateBroadcaster {
    void addStorageUpdateListener(StorageUpdateListener listener);
    void removeStorageUpdateListener(StorageUpdateListener listener);
    void notifyStorageUpdate(StorageUpdateMessage message);
    void onRegisterForStorageUpdate(StorageUpdateListener listener);
}
