package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Storage;

import java.util.List;

public abstract class View extends Observable<CommandMessage> {
    // What we might want to draw
    protected Player me;
    protected Board board;
    protected Storage storage;
    String msg;
    List<CommandMessage> nextMoves;

    // We can't implement multiple instances of Observer<>, so instantiate them separately
    private BoardObserver boardObserver;
    private StorageObserver storageObserver;
    private ResponseObserver responseObserver;
    public BoardObserver getBoardObserver() {
        return boardObserver;
    }
    public StorageObserver getStorageObserver() {
        return storageObserver;
    }
    public Observer<CommandResponse> getResponseObserver() {
        return responseObserver;
    }

    private class BoardObserver implements Observer<Board> {
        @Override
        public void onChange(Board message) {
            board = message;
            redraw();
        }
    }

    private class StorageObserver implements Observer<Storage> {
        @Override
        public void onChange(Storage message) {
            storage = message;
            redraw();
        }
    }

    private class ResponseObserver implements Observer<CommandResponse> {
        @Override
        public void onChange(CommandResponse message) {
            msg = message.getMessage();
            nextMoves = message.getAvailableNextMoves();
            redraw();
        }
    }

    public View(Player me) {
        this.me = me;
        this.boardObserver = new BoardObserver();
        this.storageObserver = new StorageObserver();
        this.responseObserver = new ResponseObserver();
        // Maybe a better solution would be to observe a single object, for example a Pair<Board, Storage>,
        // then you can notify just one with setting the other to null...
    }

    public abstract void redraw();
}
