package it.polimi.ingsw.Utils;

import it.polimi.ingsw.Game.Player;

public class Log {
    /**
     * Print a nicely formatted log for a player action
     *
     * @param p the player this log is associated with
     * @param log a string message representing the action
     */
    public static void logPlayerAction(Player p, String log) {
        System.out.println(" * " + p.toString() + " " + log);
    }

    /**
     * Print a nicely formatted log for a failed player action
     *
     * @param p the player this log is associated with
     * @param action the action that failed
     * @param reason the reason for the failure
     */
    public static void logInvalidAction(Player p, String action, String reason) {
        logPlayerAction(p, "issued an invalid `" + action + "` command: " + (reason != null ? reason : ""));
    }
}
