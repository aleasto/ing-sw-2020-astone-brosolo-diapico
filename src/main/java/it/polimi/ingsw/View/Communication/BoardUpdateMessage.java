package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Board;

public class BoardUpdateMessage extends Message {
    // TODO: Make this slimmer
    private final Board board;

    public BoardUpdateMessage(Board board) {
        Board tempBoard;
        try {
            tempBoard = board.clone();
        } catch (CloneNotSupportedException e) {
            tempBoard = null;
            e.printStackTrace();
        }
        this.board = tempBoard;
    }

    public Board getBoard() {
        return board;
    }
}
