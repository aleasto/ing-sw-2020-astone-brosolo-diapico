package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class EndGameEventMessage extends Message {
    private final Player winner;
    private final int lobbyClosingDelay;

    public EndGameEventMessage(Player winner, int lobbyClosingDelay) {
        // No winner means the game has been interrupted
        if (winner != null) {
            Player tmp;
            try {
                tmp = winner.clone();
            } catch (CloneNotSupportedException e) {
                System.out.println("Could not clone " + winner + ": " + e.getMessage());
                System.out.println("Falling back to serializing shared object");
                tmp = winner;
            }
            this.winner = tmp;
        } else {
            this.winner = null;
        }

        this.lobbyClosingDelay = lobbyClosingDelay;
    }

    public Player getWinner() {
        return winner;
    }

    public long getLobbyClosingDelay() {
        return lobbyClosingDelay;
    }
}
