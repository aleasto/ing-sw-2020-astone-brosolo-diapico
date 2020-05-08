package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.View.Communication.*;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLIView extends View implements Runnable {
    private Scanner stdin = new Scanner(System.in);
    private PrintStream stdout = new PrintStream(System.out);

    private Board board;
    private Storage storage;
    private List<MoveCommandMessage> nextMoves;
    private List<BuildCommandMessage> nextBuilds;
    private String textMessage = "";
    private List<Player> playerList;
    private List<String> gods;

    public CLIView(Player me) {
        super(me);
    }

    public void redraw() {
        // Clean terminal
        stdout.print("\033[H\033[2J");
        stdout.flush();

        // Print connected players
        if (playerList != null) {
            stdout.print("Connected players: ");
            for (Player p : playerList) {
                stdout.print(p.getName() + ", ");
            }
            stdout.print("\n");
        }

        // Print storage
        if (storage != null) {
            stdout.print("Available pieces: ");
            stdout.print("Lvl0: " + twoDigits(storage.getAvailable(0)) +
                    " | Lvl1: " + twoDigits(storage.getAvailable(1)) +
                    " | Lvl2: " + twoDigits(storage.getAvailable(2)) +
                    " | Domes: " + twoDigits(storage.getAvailable(3)) +
                    "\n");
        }

        // Print board
        if (board != null) {
            for (int i = 0; i < board.getDimX(); i++) {
                for (int j = 0; j < board.getDimY(); j++) {
                    Tile tile = board.getAt(i, j);
                    if (tile.getOccupant() != null) stdout.print("\u001B[31m");
                    stdout.print((tile.hasDome() ? "x" : tile.getHeight()) + " ");
                    stdout.print("\u001B[0m");
                }
                stdout.print("\n");
            }
        }

        // Print message
        for (int i = 0; i < 100; i++)
            stdout.print("-");
        stdout.print("\n");
        stdout.print("Message: " + textMessage + "\n");
        for (int i = 0; i < 100; i++)
            stdout.print("-");
        stdout.print("\n");

        // Print god pool selection
        if (gods != null) {
            stdout.print("Available gods: " + gods.stream().collect(Collectors.joining(", ")) + "\n");
            for (int i = 0; i < 100; i++)
                stdout.print("-");
            stdout.print("\n");
        }

        // Print next available moves
        if (nextMoves != null && nextBuilds != null) {
            stdout.print("Next available options:\n");
            stdout.print("Move: ");
            stdout.print(nextMoves.stream()
                    .map(action -> action.getFromX() + "," + action.getFromY() + "->" + action.getToX() + "," + action.getToY())
                    .collect(Collectors.joining(";  ")));
            stdout.print("\n");
            stdout.print("Build: ");
            stdout.print(nextBuilds.stream()
                    .map(action -> action.getFromX() + "," + action.getFromY() + "->" + action.getToX() + "," + action.getToY() + " lvl" + action.getBlock())
                    .collect(Collectors.joining(";  ")));
            stdout.print("\n");
            for (int i = 0; i < 100; i++)
                stdout.print("-");
            stdout.print("\n");
            stdout.flush();
        }
    }

    @Override
    public void run() {
        stdin.useDelimiter("\n");
        while(true) {
            String current = stdin.next();
            try {
                handleInput(current);
            } catch (InvalidCommandException ex) {
                textMessage = ex.getMessage();
                redraw();
            }
        }
    }

    private void handleInput(String str) throws InvalidCommandException {
        Scanner lineScanner = new Scanner(str);
        String command;
        try {
            command = lineScanner.nextLine();
        } catch (Exception ex) {
            throw new InvalidCommandException("Invalid input");
        }

        Scanner commandScanner = new Scanner(command);
        commandScanner.useDelimiter("[,\\s]+"); // separators are comma, space and newline
        String commandName = commandScanner.next();
        switch (commandName.toLowerCase()) {
            case "move":
                notifyCommand(MoveCommandMessage.fromScanner(commandScanner));
                break;
            case "build":
                notifyCommand(BuildCommandMessage.fromScanner(commandScanner));
                break;
            case "endturn":
                notifyCommand(new EndTurnCommandMessage());
                break;
            case "start":
                notifyCommand(new StartGameCommandMessage());
                break;
            case "godpool":
                notifyCommand(SetGodPoolCommandMessage.fromScanner(commandScanner));
                break;
            case "god":
                notifyCommand(SetGodCommandMessage.fromScanner(commandScanner));
                break;
            default:
                throw new InvalidCommandException("`" + commandName + "` is not a valid action");
        }
    }

    private String twoDigits(int in) {
        return String.format("%02d", in);
    }

    @Override
    public void onBoardUpdate(BoardUpdateMessage message) {
        this.board = message.getBoard();
        redraw();
    }

    @Override
    public void onNextActionsUpdate(NextActionsUpdateMessage message) {
        this.nextMoves = message.getNextMoves();
        this.nextBuilds = message.getNextBuilds();
        redraw();
    }

    @Override
    public void onStorageUpdate(StorageUpdateMessage message) {
        this.storage = message.getStorage();
        redraw();
    }

    @Override
    public void onText(TextMessage message) {
        this.textMessage = message.getText();
        redraw();
    }

    @Override
    public void onPlayersUpdate(PlayersUpdateMessage message) {
        this.playerList = message.getPlayerList();
        redraw();
    }

    @Override
    public void onShowGods(GodListMessage message) {
        this.gods = message.getGods();
        redraw();
    }
}
