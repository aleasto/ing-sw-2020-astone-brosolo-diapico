package it.polimi.ingsw.Utils;

import it.polimi.ingsw.Game.Player;

public class Log {
    public static void logPlayerAction(Player p, String log) {
        System.out.println(" * " + p.toString() + " " + log);
    }

    public static void logInvalidAction(Player p, String action, String reason) {
        logPlayerAction(p, "issued an invalid `" + action + "` command: " + (reason != null ? reason : ""));
    }
}
