package it.polimi.ingsw.View;

import java.util.List;

public class CommandResponse {
    private String message;
    private List<CommandMessage> availableNextMoves;

    public CommandResponse(String message) {
        this(message, null);
    }

    public CommandResponse(String message, List<CommandMessage> next) {
        this.message = message;
        this.availableNextMoves = next;
    }

    public String getMessage() {
        return message;
    }

    public List<CommandMessage> getAvailableNextMoves() {
        return availableNextMoves;
    }
}
