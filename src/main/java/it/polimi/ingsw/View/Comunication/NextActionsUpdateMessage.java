package it.polimi.ingsw.View.Comunication;

import java.util.List;

public class NextActionsUpdateMessage extends Message {
    private final List<MoveCommandMessage> nextMoves;
    private final List<BuildCommandMessage> nextBuilds;

    public NextActionsUpdateMessage(List<MoveCommandMessage> nextMoves, List<BuildCommandMessage> nextBuilds) {
        this.nextMoves = nextMoves;
        this.nextBuilds = nextBuilds;
    }

    public List<MoveCommandMessage> getNextMoves() {
        return nextMoves;
    }

    public List<BuildCommandMessage> getNextBuilds() {
        return nextBuilds;
    }
}
