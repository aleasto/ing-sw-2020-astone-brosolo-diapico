package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;

import java.io.PrintStream;
import java.util.Scanner;

public class CLIView extends View implements Runnable {
    private Scanner stdin = new Scanner(System.in);
    private PrintStream stdout = new PrintStream(System.out);

    public CLIView(Player me) {
        super(me);
    }

    @Override
    public void redraw() {
        // TODO: draw player (name & god), draw board, draw storage, draw message
        stdout.print("\033[H\033[2J");
        stdout.flush();
        stdout.print("YES\n");
        stdout.flush();
    }

    @Override
    public void run() {
        stdin.useDelimiter("\n");
        while(true) {
            String current = stdin.next();
            try {
                CommandMessage cmd = CommandMessage.parseCommand(me, current);
                notifyChange(cmd);
            } catch (IllegalArgumentException ex) {
                errorMessage = ex.getMessage();
                redraw();
            }
        }
    }
}
