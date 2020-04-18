package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;

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
        if (storage == null || board == null)
            return;

        // Clean terminal
        stdout.print("\033[H\033[2J");
        stdout.flush();

        // Print storage
        stdout.print("Available pieces: ");
        stdout.print("Lvl0: " + twoDigits(storage.getAvailable(0)) +
                " | Lvl1: " + twoDigits(storage.getAvailable(1)) +
                " | Lvl2: " + twoDigits(storage.getAvailable(2)) +
                " | Domes: " + twoDigits(storage.getAvailable(3)) +
                "\n");

        // Print board
        for (int i = 0; i < board.getDimX(); i++) {
            for (int j = 0; j < board.getDimY(); j++) {
                Tile tile = board.getAt(i, j);
                if (tile.getOccupant() != null) stdout.print("\u001B[31m");
                stdout.print((tile.hasDome() ? "x" : tile.getHeight()) + " ");
                stdout.print("\u001B[0m");
            }
            stdout.print("\n");
        }

        // Print message
        for (int i = 0; i < 100; i++)
            stdout.print("-");
        stdout.print("\n");
        stdout.print("Message: " + errorMessage + "\n");
        for (int i = 0; i < 100; i++)
            stdout.print("-");
        stdout.print("\n");
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
            } catch (InvalidCommandException ex) {
                errorMessage = ex.getMessage();
                redraw();
            }
        }
    }

    private String twoDigits(int in) {
        return String.format("%02d", in);
    }
}
