package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Board;

public class BoardUpdateMessage extends Message {
    // TODO: Make this slimmer
    private final Board board;

    public BoardUpdateMessage(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
