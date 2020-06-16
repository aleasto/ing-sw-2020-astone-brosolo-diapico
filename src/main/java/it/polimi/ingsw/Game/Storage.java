package it.polimi.ingsw.Game;

import it.polimi.ingsw.View.Communication.Broadcasters.StorageUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.StorageUpdateListener;
import it.polimi.ingsw.View.Communication.StorageUpdateMessage;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Storage implements StorageUpdateBroadcaster, Serializable, Cloneable {

    private Integer[] pieceAmt;

    /**
     * Create a storage object
     * @param amts an array of integers that represents how many types of blocks, and how many items per each block.
     */
    public Storage(Integer ...amts) {
        this.pieceAmt = amts;
    }

    /**
     * @return how many block types we track
     */
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

    /**
     * Create a deep clone of this object
     * @return a new object that exposes the same block types and block availability as this
     * @throws CloneNotSupportedException if any field is not cloneable
     */
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
