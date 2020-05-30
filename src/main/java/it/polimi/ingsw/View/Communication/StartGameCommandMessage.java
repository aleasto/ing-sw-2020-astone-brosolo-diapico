package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StartGameCommandMessage extends CommandMessage {
    private boolean playWithGods = true;

    public StartGameCommandMessage() {}
    public StartGameCommandMessage(boolean playWithGods) {
        this.playWithGods = playWithGods;
    }

    public static StartGameCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        StartGameCommandMessage message = new StartGameCommandMessage();

        Map<String, String> opts = new HashMap<>();
        while (in.hasNext()) {
            String optName = in.next();
            if (!in.hasNext())
                throw new InvalidCommandException("Invalid options to `start`");
            opts.put(optName, in.next());
        }

        for (Map.Entry<String, String> opt : opts.entrySet()) {
            switch (opt.getKey()) {
                case "--gods":
                    switch (opt.getValue()) {
                        case "on":
                            message.playWithGods = true;
                            break;
                        case "off":
                            message.playWithGods = false;
                            break;
                        default:
                            throw new InvalidCommandException("Invalid `--gods` argument");
                    }
                    break;
                default:
                    throw new InvalidCommandException("Invalid option: `" + opt.getKey() + "`");
            }
        }

        return message;
    }

    public boolean getPlayWithGods() {
        return playWithGods;
    }

    @Override
    public String toString() {
        return "start";
    }
}
