package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Board;

public class BoardUpdateMessage extends Message {
    private final Board board;

    public BoardUpdateMessage(Board board) {
        Board tempBoard;
        try {
            tempBoard = board.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Could not clone " + board + ": " + e.getMessage());
            System.out.println("Falling back to serializing shared object");
            tempBoard = null;
        }
        this.board = tempBoard;
    }

    public Board getBoard() {
        return board;
    }
}
