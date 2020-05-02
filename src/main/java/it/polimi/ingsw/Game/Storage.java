package it.polimi.ingsw.Game;

import it.polimi.ingsw.View.Comunication.Dispatchers.StorageUpdateDispatcher;
import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;
import it.polimi.ingsw.View.Comunication.StorageUpdateMessage;

import java.util.ArrayList;
import java.util.List;

public class Storage implements StorageUpdateDispatcher {
    private static final int MAX_LVL0 = 22;
    private static final int MAX_LVL1 = 18;
    private static final int MAX_LVL2 = 14;
    private static final int MAX_DOME = 14;

    private int pieceAmt[] = new int[] { MAX_LVL0, MAX_LVL1, MAX_LVL2, MAX_DOME };

    /**
     * Retrieve a piece from the storage
     * @param piece the piece type
     * @return false if no available piece of the given type, true otherwise
     */
    public boolean retrieve(int piece) {
        if (piece > pieceAmt.length - 1 || piece < 0)
            return false;

        if (pieceAmt[piece] > 0) {
            pieceAmt[piece]--;
            notifyStorageUpdate(new StorageUpdateMessage(this));
            return true;
        }
        return false;
    }

    /**
     * Get a piece type availability for display purposes
     * @param piece the piece type
     * @return the number of available pieces of `piece`
     */
    public int getAvailable(int piece) {
        if (piece > pieceAmt.length - 1 || piece < 0)
            return 0;

        return pieceAmt[piece];
    }

    List<StorageUpdateListener> listeners = new ArrayList<>();
    @Override
    public void addStorageUpdateListener(StorageUpdateListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
        onRegisterForStorageUpdate(listener);
    }
    @Override
    public void removeStorageUpdateListener(StorageUpdateListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    @Override
    public void notifyStorageUpdate(StorageUpdateMessage message) {
        synchronized (listeners) {
            for (StorageUpdateListener listener : listeners) {
                listener.onStorageUpdate(message);
            }
        }
    }
    @Override
    public void onRegisterForStorageUpdate(StorageUpdateListener listener) {
        listener.onStorageUpdate(new StorageUpdateMessage(this));
    }
}
