package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SetGodPoolCommandMessage extends CommandMessage {
    private List<String> godPool;

    public SetGodPoolCommandMessage(List<String> godPool) {
        this.godPool = godPool;
    }

    public List<String> getGodPool() {
        return godPool;
    }

    public static SetGodPoolCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        try {
            List<String> gods = new ArrayList<String>();
            while (in.hasNext()) {
                gods.add(in.next());
            }
            return new SetGodPoolCommandMessage(gods);
        } catch (Exception ex) {
            throw new InvalidCommandException("Invalid parameters for action `godpool`");
        }
    }
}
