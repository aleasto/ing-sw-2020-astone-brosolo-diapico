package it.polimi.ingsw.Game;

import it.polimi.ingsw.View.Communication.Broadcasters.StorageUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.StorageUpdateListener;
import it.polimi.ingsw.View.Communication.StorageUpdateMessage;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Storage implements StorageUpdateBroadcaster, Serializable, Cloneable {

    private Integer[] pieceAmt;

    public Storage(Integer ...amts) {
        this.pieceAmt = amts;
    }

    // Just for tests
    // TODO: Remove this
    public Storage() {
        this(22, 18, 14, 14);
    }

    public int getPieceTypes() {
        return pieceAmt.length;
    }

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

    @Override
    public Storage clone() throws CloneNotSupportedException {
        Storage clone = (Storage) super.clone();
        clone.pieceAmt = this.pieceAmt.clone();
        return clone;
    }

    final transient List<StorageUpdateListener> listeners = new ArrayList<>();
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
