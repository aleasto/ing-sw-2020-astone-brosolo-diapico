package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.View.Communication.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CLIView extends ClientRemoteView implements Runnable {
    private Scanner stdin = new Scanner(System.in);
    private PrintStream stdout = new PrintStream(System.out);

    private Board board;
    private Storage storage;
    private List<MoveCommandMessage> nextMoves;
    private List<BuildCommandMessage> nextBuilds;
    private String textMessage = "";
    private List<Player> playerList;
    private Player currentTurnPlayer;
    private List<String> gods;

    private HashMap<Player, Function<String, String>> colors = new HashMap();

    public CLIView(Player me) {
        super(me);
        reset("Hi, " + getPlayer().getName() + ". Connect via `connect ip lobby`");
    }

    public void reset(String msg) {
        board = null;
        storage = null;
        nextMoves = null;
        playerList = null;
        currentTurnPlayer = null;
        gods = null;

        onText(new TextMessage(msg));
    }

    public void redraw() {
        // Clean terminal
        stdout.print("\033[H\033[2J");
        stdout.flush();

        // Print connected players
        if (playerList != null) {
            stdout.println("Connected players: " +
                    playerList.stream().map(p -> {
                        String coloredName = colors.get(p).apply(p.getName());
                        return p.equals(currentTurnPlayer) ? Color.UNDERLINE(Color.BOLD(coloredName)) : coloredName;
                    }).collect(Collectors.joining(", ")));
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
            stdout.print(Color.UNDERLINE("x\\y|"));
            for (int i = 0; i < board.getDimX(); i++) {
                stdout.print(Color.UNDERLINE(i + " "));
            }
            stdout.print("\n");
            for (int i = 0; i < board.getDimX(); i++) {
                stdout.print(" " + i + " |");
                for (int j = 0; j < board.getDimY(); j++) {
                    Tile tile = board.getAt(i, j);
                    Worker w = tile.getOccupant();
                    Function<String, String> colorFun = (w == null ? Color::NONE : colors.get(w.getOwner()));
                    String symbol = tile.hasDome() ? "x" : Integer.toString(tile.getHeight());
                    stdout.print(colorFun.apply(symbol) + " ");
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

    private String twoDigits(int in) {
        return String.format("%02d", in);
    }

    @Override
    public void run() {
        while (true) {
            String current = stdin.nextLine();
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
        if (!isConnected()) {
            switch (commandName.toLowerCase()) {
                case "connect":
                    try {
                        connect(commandScanner.next(), commandScanner.next());
                        startNetworkThread();
                    } catch (Exception ex) {
                        throw new InvalidCommandException("Invalid network parameters");
                    }
                    break;
                default:
                    throw new InvalidCommandException("You must connect first.");
            }
        } else {
            switch (commandName.toLowerCase()) {
                case "move":
                    onCommand(MoveCommandMessage.fromScanner(commandScanner));
                    break;
                case "build":
                    onCommand(BuildCommandMessage.fromScanner(commandScanner));
                    break;
                case "endturn":
                    onCommand(new EndTurnCommandMessage());
                    break;
                case "start":
                    onCommand(new StartGameCommandMessage());
                    break;
                case "godpool":
                    onCommand(SetGodPoolCommandMessage.fromScanner(commandScanner));
                    break;
                case "god":
                    onCommand(SetGodCommandMessage.fromScanner(commandScanner));
                    break;
                case "place":
                    onCommand(PlaceWorkerCommandMessage.fromScanner(commandScanner));
                    break;
                case "disconnect":
                    disconnect();
                    break;
                default:
                    throw new InvalidCommandException("`" + commandName + "` is not a valid action");
            }
        }
    }

    @Override
    public void onDisconnect() {
        stopNetworkThread();
        disconnect();
        reset("Connection dropped. You may connect again with `connect ip lobby`");
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
        for (Player p : playerList) {
            if (!colors.containsKey(p)) {
                colors.put(p, Color.uniqueColor());
            }
        }
        redraw();
    }

    @Override
    public void onShowGods(GodListMessage message) {
        this.gods = message.getGods();
        redraw();
    }

    @Override
    public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
        this.currentTurnPlayer = message.getPlayer();
        redraw();
    }
}
