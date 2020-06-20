package it.polimi.ingsw.Client;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.*;
import it.polimi.ingsw.Game.Actions.GodInfo;
import it.polimi.ingsw.Utils.CLIColor;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CLI {
    ClientRemoteView internalView;

    private final Scanner stdin = new Scanner(System.in);
    private final PrintStream stdout = new PrintStream(System.out);

    private ParserState parserState;

    private Board board;
    private Storage storage;
    private List<MoveCommandMessage> nextMoves;
    private List<BuildCommandMessage> nextBuilds;
    private String textMessage = "";
    private List<Player> playerList;
    private List<Player> spectatorList;
    private Player currentTurnPlayer;
    private List<String> gods;
    private Set<LobbyInfo> lobbies;
    private String ip;
    private int port;
    private boolean lost;
    private Set<GodInfo> knownGods = new HashSet<>();

    private final HashMap<Player, Function<String, String>> colors = new HashMap<>();

    // For easy debugging
    public static void main(String[] args) {
        new CLI().start();
    }

    /**
     * Start the client.
     * Asks for a player and instantiates the remote view
     */
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
            String line = "";
            try {
                line = stdin.nextLine();
            } catch (NoSuchElementException ignored) {
                /* Windows is fun */
                System.exit(1);
            }
            Scanner commandScanner = new Scanner(line);
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
            public void onEndGameEvent(EndGameEventMessage message) {
                String msg = "The lobby will close and you will be disconnected in " +
                        message.getLobbyClosingDelay() + " seconds";
                Player winner = message.getWinner();
                if (winner != null && winner.equals(getPlayer())) {
                    onText(new TextMessage("You have won!\n" + msg));
                } else if (winner != null) {
                    String coloredPlayerName = colors.get(winner).apply(winner.getName());
                    onText(new TextMessage("Player " + coloredPlayerName + " has won!\n" + msg));
                } else {
                    onText(new TextMessage("The game has ended because someone disconnected.\n" + msg));
                }
            }

            @Override
            public void onDisconnect() {
                reset("Connection dropped. You may connect again with `connect <ip> <port>`");
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
                if (lost)
                    textMessage = "You lost!\n" + textMessage;
                redraw();
            }

            @Override
            public void onPlayersUpdate(PlayersUpdateMessage message) {
                spectatorList = message.getSpectatorList();
                playerList = message.getPlayerList();
                for (Player p : playerList) {
                    if (!colors.containsKey(p)) {
                        colors.put(p, CLIColor.uniqueColor());
                    }
                }
                redraw();
            }

            @Override
            public void onShowGods(GodListMessage message) {
                if (message.getGods() == null || message.getHowManyToChoose() == 0)
                    gods = null;
                else {
                    gods = message.getGods().stream().map(GodInfo::getName).collect(Collectors.toList());
                    knownGods.addAll(message.getGods());
                }
                redraw();
            }

            @Override
            public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
                currentTurnPlayer = message.getPlayer();
                redraw();
            }

            @Override
            public void onPlayerLoseEvent(PlayerLoseEventMessage message) {
                Player losingPlayer = message.getPlayer();
                if (this.getPlayer().equals(losingPlayer)) {
                    lost = true;
                    onText(new TextMessage("You may continue to watch others play."));
                } else {
                    String coloredPlayerName = colors.get(losingPlayer).apply(losingPlayer.getName());
                    onText(new TextMessage("Player " + coloredPlayerName + " has lost. Their workers have been removed"));
                }
            }

            @Override
            public void onLobbiesUpdate(LobbiesUpdateMessage message) {
                lobbies = message.getLobbies();
                redraw();
            }

            @Override
            public void onPlayerChoseGodEvent(PlayerChoseGodEventMessage message) {
                try {
                    playerList.get(playerList.indexOf(message.getPlayer())).setGodName(message.getGod().getName());
                } catch (IndexOutOfBoundsException | NullPointerException ignored) {}
            }
        };

        reset("Hi, " + player.getName() + ". Connect via `connect <ip> <port>`");
        parserState = new DisconnectedParserState();
        inputLoop();
    }

    /**
     * Restore client state to the beginning.
     *
     * @param msg a message to show the user
     */
    public void reset(String msg) {
        board = null;
        storage = null;
        nextMoves = null;
        playerList = null;
        spectatorList = null;
        currentTurnPlayer = null;
        gods = null;
        lobbies = null;
        lost = false;
        knownGods = new HashSet<GodInfo>();
        parserState = new DisconnectedParserState();

        colors.clear();
        CLIColor.reset();

        textMessage = msg;
        redraw();
    }

    /**
     * Synchronously begin listening to stdin events
     */
    public void inputLoop() {
        while (true) {
            try {
                String current = stdin.nextLine();
                handleInput(current);
            } catch (InvalidCommandException ex) {
                internalView.onText(new TextMessage(ex.getMessage()));
            } catch (NoSuchElementException ignored) {
                /* Windows is fun */
            }
        }
    }

    /**
     * Clears and redraws the whole screen, line by line
     */
    public synchronized void redraw() {
        // Clean terminal
        stdout.print("\033[H\033[2J");
        stdout.flush();

        // Print connected players
        if (playerList != null) {
            stdout.println("Connected players: " +
                    playerList.stream().map(p -> {
                        String coloredName = colors.getOrDefault(p, CLIColor::NONE).apply(p.getName());
                        String playerString = p.equals(currentTurnPlayer) ? CLIColor.UNDERLINE(CLIColor.BOLD(coloredName)) : coloredName;
                        if (p.getGodName() != null)
                            playerString += "(" + p.getGodName() + ")";
                        return playerString;
                    }).collect(Collectors.joining(", ")));
        }
        if (spectatorList != null) {
            stdout.println("Spectators: " + spectatorList.stream().map(Player::getName)
                    .collect(Collectors.joining(", ")));
        }

        // Print storage
        if (storage != null) {
            stdout.print("Available pieces: ");
            for (int piece = 0; piece < storage.getPieceTypes() - 1; piece++) {
                stdout.print("Lvl" + piece + ": " + twoDigits(storage.getAvailable(piece)) + " | ");
            }
            stdout.print("Domes: " + twoDigits(storage.getAvailable(storage.getPieceTypes() - 1)));
            stdout.print("\n");
        }

        // Print board
        if (board != null) {
            stdout.print(CLIColor.UNDERLINE("x\\y|"));
            for (int i = 0; i < board.getDimY(); i++) {
                stdout.print(CLIColor.UNDERLINE(i + " "));
            }
            stdout.print("\n");
            for (int i = 0; i < board.getDimX(); i++) {
                stdout.print(" " + i + " |");
                for (int j = 0; j < board.getDimY(); j++) {
                    Tile tile = board.getAt(i, j);
                    Worker w = tile.getOccupant();
                    Function<String, String> colorFun = (w == null ? CLIColor::NONE : colors.getOrDefault(w.getOwner(), CLIColor::NONE));
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
        if (lobbies != null) {
            int defaultNameLength = 30;
            int maxNameLength = longest(lobbies.stream().map(LobbyInfo::getName).collect(Collectors.toList()));
            int printedNameLength = Math.max(maxNameLength, defaultNameLength);
            String header = "| " + padTo("Name", printedNameLength) + " | Players | Spectators | Running |";
            stdout.println(CLIColor.UNDERLINE(padTo("  Lobbies", header.length())));
            stdout.println(header.replaceAll("\\w", CLIColor.BOLD("$0")));
            if (lobbies.size() > 0) {
                for (LobbyInfo lobby : lobbies) {
                    stdout.println(CLIColor.UNDERLINE("| " + padTo(lobby.getName(), printedNameLength) +
                            " | " + padTo(Integer.toString(lobby.getPlayers()), "Players".length()) +
                            " | " + padTo(Integer.toString(lobby.getSpectators()), "Spectators".length()) +
                            " | " + padTo(lobby.getGameRunning() ? "Yes" : "No", "Running".length()) + " |"));
                }
            } else {
                stdout.println(CLIColor.UNDERLINE(padTo("| ", header.length() - 2) + " |"));
            }
        }

        // Print god pool selection
        if (gods != null) {
            stdout.print("Available gods: " + String.join(", ", gods) + "\n");
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

    /**
     * Format an integer into a two digits - base ten string
     * @param in the integer
     * @return the formatted string
     */
    private String twoDigits(int in) {
        return String.format("%02d", in);
    }

    /**
     * Find the longest string in a list
     * @param in a list of strings
     * @return the length of the longest string
     */
    private int longest(List<String> in) {
        return in.stream().mapToInt(String::length).max().orElse(0);
    }

    /**
     * Pad a string with spaces to a given length.
     * If the desired length is shorted the the current length of the string, do nothing.
     * @param s the input string
     * @param length the desired length
     * @return the input string padded with spaces
     */
    private String padTo(String s, int length) {
        StringBuilder out = new StringBuilder(s);
        for (int i = 0; i < length - s.length(); i++) {
            out.append(" ");
        }
        return out.toString();
    }

    /**
     * Parse a textual input into a command
     * @param str the input
     * @throws InvalidCommandException if the input does not represent a valid command
     */
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
        try {
            parserState.handleInput(commandScanner);
        } catch (NoSuchElementException ex) {
            throw new InvalidCommandException("Incomplete input");
        }
    }

    public class DisconnectedParserState implements ParserState {
        @Override
        public void handleInput(Scanner commandScanner) throws InvalidCommandException {
            String commandName = commandScanner.next();
            switch (commandName.toLowerCase()) {
                case "connect":
                    try {
                        try {
                            ip = commandScanner.next();
                            port = Integer.parseInt(commandScanner.next());
                        } catch (NumberFormatException ex) {
                            throw new InvalidCommandException("Port was not a number");
                        }
                        internalView.connect(ip, port);
                        internalView.startNetworkThread();
                        parserState = new JoinLobbyParserState();
                        internalView.onText(new TextMessage("Ok! Now join a lobby with `join <lobby_name>`"));
                    } catch (IOException ex) {
                        throw new InvalidCommandException("No server at " + ip + ":" + port);
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
                case "spectator":
                    internalView.onCommand(SetSpectatorCommandMessage.fromScanner(commandScanner));
                    break;
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
                    internalView.onCommand(StartGameCommandMessage.fromScanner(commandScanner));
                    break;
                case "godpool":
                    internalView.onCommand(SetGodPoolCommandMessage.fromScanner(commandScanner));
                    break;
                case "god":
                    internalView.onCommand(SetGodCommandMessage.fromScanner(commandScanner));
                    break;
                case "godinfo":
                    String godName = commandScanner.next();
                    List<GodInfo> matches = knownGods.stream().filter(i -> i.getName().equals(godName)).collect(Collectors.toList());
                    if (matches.size() > 0)
                        internalView.onText(new TextMessage(godName + ": " + matches.get(0).getDescription()));
                    else
                        throw new InvalidCommandException("Unknown god `" + godName + "`");
                    break;
                case "place":
                    internalView.onCommand(PlaceWorkerCommandMessage.fromScanner(commandScanner));
                    break;
                case "disconnect":
                    internalView.disconnect();
                    parserState = new DisconnectedParserState();
                    break;
                case "leave":
                    internalView.disconnect();
                    try {
                        internalView.connect(ip, port);
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
