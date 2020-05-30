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
                    playerList.get(playerList.indexOf(message.getPlayer())).setGodName(message.getGod());
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    //TODO: Hide this?
                    e.printStackTrace();
                }
            }
        };

        reset("Hi, " + player.getName() + ". Connect via `connect <ip> <port>`");
        parserState = new DisconnectedParserState();
        inputLoop();
    }

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
        parserState = new DisconnectedParserState();

        colors.clear();
        Color.reset();

        textMessage = msg;
        redraw();
    }

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

    public synchronized void redraw() {
        // Clean terminal
        stdout.print("\033[H\033[2J");
        stdout.flush();

        // Print connected players
        if (playerList != null) {
            stdout.println("Connected players: " +
                    playerList.stream().map(p -> {
                        String coloredName = colors.getOrDefault(p, Color::NONE).apply(p.getName());
                        String playerString = p.equals(currentTurnPlayer) ? Color.UNDERLINE(Color.BOLD(coloredName)) : coloredName;
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
                    Function<String, String> colorFun = (w == null ? Color::NONE : colors.getOrDefault(w.getOwner(), Color::NONE));
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
            stdout.println(Color.UNDERLINE(padTo("  Lobbies", header.length())));
            stdout.println(header.replaceAll("\\w", Color.BOLD("$0")));
            if (lobbies.size() > 0) {
                for (LobbyInfo lobby : lobbies) {
                    stdout.println(Color.UNDERLINE("| " + padTo(lobby.getName(), printedNameLength) +
                            " | " + padTo(Integer.toString(lobby.getPlayers()), "Players".length()) +
                            " | " + padTo(Integer.toString(lobby.getSpectators()), "Spectators".length()) +
                            " | " + padTo(lobby.getGameRunning() ? "Yes" : "No", "Running".length()) + " |"));
                }
            } else {
                stdout.println(Color.UNDERLINE(padTo("| ", header.length() - 2) + " |"));
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

    private int longest(List<String> in) {
        return in.stream().mapToInt(String::length).max().orElse(0);
    }

    private String padTo(String s, int length) {
        StringBuilder out = new StringBuilder(s);
        for (int i = 0; i < length - s.length(); i++) {
            out.append(" ");
        }
        return out.toString();
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
