package it.polimi.ingsw.Server;

import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.Utils.SocketInfo;
import it.polimi.ingsw.Utils.Utils;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Broadcasters.LobbiesUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.LobbiesUpdateListener;
import it.polimi.ingsw.View.RemoteView;
import org.json.JSONException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.stream.Collectors;

public class Server implements LobbiesUpdateBroadcaster {
    private ConfReader confReader;
    private final Map<String, Lobby> lobbies = new HashMap<>();

    // For easy debugging
    public static void main(String[] args) {
        new Server().start();
    }

    public Server() {
        try {
            this.confReader = new ConfReader("server.conf");
            GodFactory.loadJson();
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(confReader.getInt("port", 1234));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server started on port " + serverSocket.getLocalPort());

        while (true) {
            Socket clientSocket;
            ObjectOutputStream out;
            try {
                clientSocket = serverSocket.accept();
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("\nNew client connected");
            } catch (IOException e) {
                // go next
                continue;
            }

            // Keep connected until client joins the lobby, then it's the lobby's job
            Timer pingTimer = Utils.makeTimer(() -> trySendSync(out, new PingMessage()),
                    RemoteView.KEEP_ALIVE - RemoteView.ESTIMATED_MAX_NETWORK_DELAY);
            try {
                clientSocket.setSoTimeout(RemoteView.KEEP_ALIVE);
            } catch (SocketException ignored) {}

            // Send this client updates on the open lobbies list
            final LobbiesUpdateListener clientLobbyListener = msg -> trySendAsync(out, msg);
            addLobbiesUpdateListener(clientLobbyListener);

            // Read lobby name and connect to that
            new Thread(() -> {
                ObjectInputStream in = null;
                try {
                    in = new ObjectInputStream(clientSocket.getInputStream());
                    JoinMessage c;
                    while (true) {
                        try {
                            c = (JoinMessage) in.readObject();
                            break;
                        } catch (ClassNotFoundException | ClassCastException ignored) {}
                    }

                    removeLobbiesUpdateListener(clientLobbyListener); // Ignore more lobby list updates
                    pingTimer.cancel(); // Let the lobby handle further ping messages
                    joinLobby(c.getLobbyName(), new SocketInfo(clientSocket, out, in), c.getPlayer());
                } catch (IOException e) {
                    try {
                        clientSocket.close();
                        out.close();
                        if (in != null)
                            in.close();
                    } catch (IOException ignored) {}
                    pingTimer.cancel();
                    removeLobbiesUpdateListener(clientLobbyListener);
                    System.out.println("Client disconnected without joining any lobby");
                }
            }).start();
        }
    }

    public void joinLobby(String name, SocketInfo client, Player player) {
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

    public Set<LobbyInfo> makeLobbyInfo() {
        synchronized (lobbies) {
            return lobbies.keySet().stream().map(name -> {
                Lobby lobby = lobbies.get(name);
                return new LobbyInfo(name, lobby.getPlayerCount(), lobby.getSpectatorCount(), lobby.isGameInProgress());
            }).collect(Collectors.toSet());
        }
    }

    private final Object outLock = new Object();
    private void trySendSync(ObjectOutputStream to, Message message) {
        synchronized (outLock) {
            try {
                to.writeObject(message);
            } catch (IOException ignored) {
            }
        }
    }

    private void trySendAsync(ObjectOutputStream to, Message message) {
        new Thread(() -> trySendSync(to, message)).start();
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
