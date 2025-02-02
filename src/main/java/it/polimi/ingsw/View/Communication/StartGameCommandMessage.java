package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.GameRules;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StartGameCommandMessage extends CommandMessage {
    private final GameRules rules;

    public StartGameCommandMessage(GameRules rules) {
        this.rules = rules;
    }

    public static StartGameCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        GameRules rules = new GameRules();

        Map<String, String> opts = new HashMap<>();
        in.useDelimiter("\\s+");
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
                            rules.setPlayWithGods(true);
                            break;
                        case "off":
                            rules.setPlayWithGods(false);
                            break;
                        default:
                            throw new InvalidCommandException("Invalid `--gods` argument");
                    }
                    break;
                case "--board":
                    Scanner scanner = new Scanner(opt.getValue());
                    scanner.useDelimiter(",");
                    try {
                        rules.setBoardSize(new Pair<>(scanner.nextInt(), scanner.nextInt()));
                    } catch (NoSuchElementException ex) {
                        throw new InvalidCommandException("Invalid `--board` argument");
                    }
                    break;
                case "--blocks":
                    Integer[] blocks = Utils.parseIntsSafe(opt.getValue());
                    if (blocks.length < 2) { // can't really play with less than 2 blocks
                        throw new InvalidCommandException("Invalid `--blocks` argument: too few block types");
                    } else {
                        rules.setBlocks(blocks);
                    }
                    break;
                case "--workers":
                    try {
                        rules.setWorkers(Integer.parseInt(opt.getValue()));
                    } catch (NumberFormatException ex) {
                        throw new InvalidCommandException("Invalid `--workers` argument");
                    }
                    break;
                default:
                    throw new InvalidCommandException("Invalid option: `" + opt.getKey() + "`");
            }
        }

        return new StartGameCommandMessage(rules);
    }

    public GameRules getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return "start with " + rules.toString();
    }
}
