package it.polimi.ingsw.Server;

import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Broadcasters.LobbiesUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.LobbiesUpdateListener;
import it.polimi.ingsw.View.ServerRemoteView;
import org.json.JSONException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Server implements LobbiesUpdateBroadcaster {
    private ConfReader confReader;
    private final Map<String, Lobby> lobbies = new HashMap<>();

    private static final int DEFAULT_PORT = 1234;

    // For easy debugging
    public static void main(String[] args) {
        new Server().start();
    }

    /**
     * Creates a server.
     * Checks that configuration files exist, or creates defaults.
     * Exits if gods file is malformed, or if defaults generation fails.
     * Does not start the server.
     */
    public Server() {
        try {
            this.confReader = new ConfReader("server.conf");
            GodFactory.loadJson();
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Start the server.
     * Begin listening on port read from config file, or fall back to DEFAULT_PORT.
     * When a client connects, create a remote view and start updating him about open lobbies.
     */
    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(confReader.getInt("port", DEFAULT_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server started on port " + serverSocket.getLocalPort());

        while (true) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("\n" + clientSocket + " connected");
            } catch (IOException e) {
                // go next
                continue;
            }

            // Instantiate a remoteView that only accepts `join` messages
            ServerRemoteView remoteView = new ServerRemoteView(clientSocket);
            remoteView.setCommandListener(command -> {
                if (command instanceof JoinCommandMessage) {
                    JoinCommandMessage joinCommand = (JoinCommandMessage) command;
                    removeLobbiesUpdateListener(remoteView); // Ignore more lobby list updates
                    joinLobby(joinCommand.getLobbyName(), remoteView, joinCommand.getPlayer());
                } /* else ignore */
            });
            remoteView.setDisconnectListener(() -> {
                removeLobbiesUpdateListener(remoteView);
                System.out.println(clientSocket + " disconnected without joining any lobby");
            });
            addLobbiesUpdateListener(remoteView);

            remoteView.startNetworkThread();
        }
    }

    /**
     * Make a client join a lobby
     *
     * @param name the lobby name
     * @param client the client's remote view
     * @param player the client's created player object
     */
    public void joinLobby(String name, ServerRemoteView client, Player player) {
        synchronized (lobbies) {
            Lobby lobby = lobbies.get(name);
            if (lobby == null) {
                System.out.println("Creating lobby " + name);
                lobby = new Lobby(confReader) {
                    @Override
                    public void closeLobby() {
                        System.out.println("Destroying lobby " + name);
                        synchronized (lobbies) {
                            lobbies.remove(name);
                        }
                        notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
                    }

                    @Override
                    public void onPlayerLeave(Player p) {
                        notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
                    }

                    @Override
                    public void onSpectatorModeChanged(Player p, boolean spectator) {
                        notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
                    }

                    @Override
                    public void onGameStart(List<Player> players) {
                        System.out.println("Lobby " + name + " started with players "
                                + players.stream().map(Player::toString).collect(Collectors.joining(", ")));
                        notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
                    }
                };
                lobbies.put(name, lobby);
            }
            lobby.connect(client, player);
            notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
            Log.logPlayerAction(player, "has entered lobby " + name);
        }
    }

    /**
     * Generate lobby infos to show the user
     * @return a set of open lobbies
     */
    public Set<LobbyInfo> makeLobbyInfo() {
        synchronized (lobbies) {
            return lobbies.keySet().stream().map(name -> {
                Lobby lobby = lobbies.get(name);
                return new LobbyInfo(name, lobby.getPlayerCount(), lobby.getSpectatorCount(), lobby.isGameInProgress());
            }).collect(Collectors.toSet());
        }
    }

    private final List<LobbiesUpdateListener> lobbiesUpdateListeners = new ArrayList<>();
    @Override
    public void addLobbiesUpdateListener(LobbiesUpdateListener listener) {
        synchronized (lobbiesUpdateListeners) {
            lobbiesUpdateListeners.add(listener);
        }
        onRegisterForLobbiesUpdate(listener);
    }
    @Override
    public void removeLobbiesUpdateListener(LobbiesUpdateListener listener) {
        synchronized (lobbiesUpdateListeners) {
            lobbiesUpdateListeners.remove(listener);
        }
    }
    @Override
    public void notifyLobbiesUpdate(LobbiesUpdateMessage message) {
        synchronized (lobbiesUpdateListeners) {
            for (LobbiesUpdateListener listener : lobbiesUpdateListeners) {
                listener.onLobbiesUpdate(message);
            }
        }
    }
    @Override
    public void onRegisterForLobbiesUpdate(LobbiesUpdateListener listener) {
        listener.onLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
    }
}
