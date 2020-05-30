package it.polimi.ingsw.View.Communication;

import java.util.List;

public class NextActionsUpdateMessage extends Message {
    private final List<MoveCommandMessage> nextMoves;
    private final List<BuildCommandMessage> nextBuilds;
    private final boolean mustMove;
    private final boolean mustBuild;

    public NextActionsUpdateMessage(List<MoveCommandMessage> nextMoves, List<BuildCommandMessage> nextBuilds, boolean mustMove, boolean mustBuild) {
        this.nextMoves = nextMoves;
        this.nextBuilds = nextBuilds;
        this.mustMove = mustMove;
        this.mustBuild = mustBuild;
    }

    public List<MoveCommandMessage> getNextMoves() {
        return nextMoves;
    }

    public List<BuildCommandMessage> getNextBuilds() {
        return nextBuilds;
    }

    public boolean mustMove() {
        return mustMove;
    }

    public boolean mustBuild() {
        return mustBuild;
    }
}
