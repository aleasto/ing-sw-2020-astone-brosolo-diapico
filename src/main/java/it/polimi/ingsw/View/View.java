package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Storage;

public abstract class View extends Observable<CommandMessage> {
    // What we might want to draw
    protected Player me;
    protected Board board;
    protected Storage storage;
    protected String errorMessage;

    // We can't implement multiple instances of Observer<>, so instantiate them separately
    private BoardObserver boardObserver;
    private StorageObserver storageObserver;
    private ErrorObserver errorObserver;
    public BoardObserver getBoardObserver() {
        return boardObserver;
    }
    public StorageObserver getStorageObserver() {
        return storageObserver;
    }
    public ErrorObserver getErrorObserver() {
        return errorObserver;
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

    private class ErrorObserver implements Observer<String> {
        @Override
        public void onChange(String message) {
            errorMessage = message;
            redraw();
        }
    }

    public View(Player me) {
        this.me = me;
        this.boardObserver = new BoardObserver();
        this.storageObserver = new StorageObserver();
        this.errorObserver = new ErrorObserver();
        // Maybe a better solution would be to observe a single object, for example a Pair<Board, Storage>,
        // then you can notify just one with setting the other to null...
    }

    public abstract void redraw();
}
