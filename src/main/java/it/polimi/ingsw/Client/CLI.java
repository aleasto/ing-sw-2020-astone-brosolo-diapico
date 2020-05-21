package it.polimi.ingsw.Client;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Color;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.ParserState;
import it.polimi.ingsw.View.RemoteView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CLI {
    ClientRemoteView internalView;

    private Scanner stdin = new Scanner(System.in);
    private PrintStream stdout = new PrintStream(System.out);

    private ParserState parserState;

    private Board board;
    private Storage storage;
    private List<MoveCommandMessage> nextMoves;
    private List<BuildCommandMessage> nextBuilds;
    private String textMessage = "";
    private List<Player> playerList;
    private Player currentTurnPlayer;
    private List<String> gods;
    private String lobby;
    private Set<String> lobbies;
    private String ip;

    private final HashMap<Player, Function<String, String>> colors = new HashMap<>();

    // For easy debugging
    public static void main(String[] args) {
        new CLI().start();
    }

    public void start() {
        // Clean terminal
        stdout.print("\033[H\033[2J");
        stdout.flush();

        Scanner stdin = new Scanner(System.in);
        PrintStream stdout = new PrintStream(System.out);

        Player player;

        // For now just get player and lobby info from stdin
        stdout.println("Welcome. Who are you? `name godlikelevel`");
        while (true) {
            Scanner commandScanner = new Scanner(stdin.nextLine());
            commandScanner.useDelimiter("[,\\s]+");
            try {
                player = new Player(commandScanner.next(), commandScanner.nextInt());
                break;
            } catch (Exception ex) {
                stdout.println("Invalid command");
            }
        }

        internalView = new ClientRemoteView(player) {
            @Override
            public void onDisconnect() {
                reset("Connection dropped. You may connect again with `connect ip lobby`");
            }

            @Override
            public void onBoardUpdate(BoardUpdateMessage message) {
                board = message.getBoard();
                redraw();
            }

            @Override
            public void onNextActionsUpdate(NextActionsUpdateMessage message) {
                nextMoves = message.getNextMoves();
                nextBuilds = message.getNextBuilds();
                redraw();
            }

            @Override
            public void onStorageUpdate(StorageUpdateMessage message) {
                storage = message.getStorage();
                redraw();
            }

            @Override
            public void onText(TextMessage message) {
                textMessage = message.getText();
                redraw();
            }

            @Override
            public void onPlayersUpdate(PlayersUpdateMessage message) {
                playerList = message.getPlayerList();
                for (Player p : playerList) {
                    if (!colors.containsKey(p)) {
                        colors.put(p, Color.uniqueColor());
                    }
                }
                redraw();
            }

            @Override
            public void onShowGods(GodListMessage message) {
                gods = message.getGods();
                redraw();
            }

            @Override
            public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
                currentTurnPlayer = message.getPlayer();
                redraw();
            }

            @Override
            public void onLobbiesUpdate(LobbiesUpdateMessage message) {
                lobbies = message.getLobbyNames();
                redraw();
            }
        };

        reset("Hi, " + player.getName() + ". Connect via `connect <ip>`");
        parserState = new DisconnectedParserState();
        inputLoop(); // CLI is runnable, but we can just run it on this thread too
    }

    public void reset(String msg) {
        board = null;
        storage = null;
        nextMoves = null;
        playerList = null;
        currentTurnPlayer = null;
        gods = null;
        lobby = null;
        lobbies = null;
        ip = null;

        internalView.onText(new TextMessage(msg));
    }

    public void inputLoop() {
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

        // Print lobby list
        if (lobbies != null && lobbies.size() > 0) {
            stdout.println("Lobbies:");
            for (String name : lobbies) {
                stdout.println(" * " + name);
            }
        }

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
        parserState.handleInput(commandScanner);
    }

    public class DisconnectedParserState implements ParserState {
        @Override
        public void handleInput(Scanner commandScanner) throws InvalidCommandException {
            String commandName = commandScanner.next();
            switch (commandName.toLowerCase()) {
                case "connect":
                    try {
                        ip = commandScanner.next();
                        internalView.connect(ip);
                        internalView.startNetworkThread();
                        parserState = new JoinLobbyParserState();
                        internalView.onText(new TextMessage("Ok! Now join a lobby with `join <lobby_name>`"));
                    } catch (IOException ex) {
                        throw new InvalidCommandException("Invalid ip");
                    }
                    break;
                default:
                    throw new InvalidCommandException("You must connect first.");
            }
        }
    }
    public class JoinLobbyParserState implements ParserState {
        @Override
        public void handleInput(Scanner commandScanner) throws InvalidCommandException {
            String commandName = commandScanner.next();
            switch (commandName.toLowerCase()) {
                case "join":
                    String lobbyName = commandScanner.next();
                    internalView.join(lobbyName);
                    lobbies = null; // stop drawing lobby list
                    lobby = lobbyName;
                    parserState = new PlayingParserState();
                    break;
                case "disconnect":
                    internalView.disconnect();
                    parserState = new DisconnectedParserState();
                    break;
                default:
                    throw new InvalidCommandException("You must join a lobby first.");
            }
        }
    }
    public class PlayingParserState implements ParserState {
        @Override
        public void handleInput(Scanner commandScanner) throws InvalidCommandException {
            String commandName = commandScanner.next();
            switch (commandName.toLowerCase()) {
                case "move":
                    internalView.onCommand(MoveCommandMessage.fromScanner(commandScanner));
                    break;
                case "build":
                    internalView.onCommand(BuildCommandMessage.fromScanner(commandScanner));
                    break;
                case "endturn":
                    internalView.onCommand(new EndTurnCommandMessage());
                    break;
                case "start":
                    internalView.onCommand(new StartGameCommandMessage());
                    break;
                case "godpool":
                    internalView.onCommand(SetGodPoolCommandMessage.fromScanner(commandScanner));
                    break;
                case "god":
                    internalView.onCommand(SetGodCommandMessage.fromScanner(commandScanner));
                    break;
                case "place":
                    internalView.onCommand(PlaceWorkerCommandMessage.fromScanner(commandScanner));
                    break;
                case "disconnect":
                    internalView.disconnect();
                    parserState = new DisconnectedParserState();
                    break;
                case "leave":
                    String prevIp = ip;
                    internalView.disconnect();
                    try {
                        internalView.connect(prevIp);
                        internalView.startNetworkThread();
                        parserState = new JoinLobbyParserState();
                        internalView.onText(new TextMessage("Ok! Now join a lobby with `join <lobby_name>`"));
                    } catch (IOException e) {
                        throw new InvalidCommandException("Connection error");
                    }
                    break;
                default:
                    throw new InvalidCommandException("`" + commandName + "` is not a valid action");
            }
        }
    }


}
