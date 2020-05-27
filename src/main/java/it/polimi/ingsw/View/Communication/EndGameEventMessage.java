package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class EndGameEventMessage extends Message {
    private final Player winner;
    private final long lobbyClosingDelay;

    public EndGameEventMessage(Player winner, long lobbyClosingDelay) {
        // No winner means the game has been interrupted
        if (winner != null) {
            Player tmp;
            try {
                tmp = winner.clone();
            } catch (CloneNotSupportedException e) {
                //TODO: Log all these exceptions
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
