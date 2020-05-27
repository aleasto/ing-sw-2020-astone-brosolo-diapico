package it.polimi.ingsw.Server;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.SocketInfo;
import it.polimi.ingsw.View.Communication.Broadcasters.LobbiesUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.ConnectionMessage;
import it.polimi.ingsw.View.Communication.Listeners.LobbiesUpdateListener;
import it.polimi.ingsw.View.Communication.LobbiesUpdateMessage;
import it.polimi.ingsw.View.Communication.LobbyInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Server implements LobbiesUpdateBroadcaster {
    public static final int PORT_NUMBER = 1234;
    final Map<String, Lobby> lobbies = new HashMap<>();

    // For easy debugging
    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        System.out.println("Server started!");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nNew client connected");

                // Send this client updates on the open lobbies list
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                final LobbiesUpdateListener clientLobbyListener = msg -> {
                    try {
                        out.writeObject(msg);
                    } catch (IOException ignored) {}
                };
                addLobbiesUpdateListener(clientLobbyListener);

                // Read lobby name and connect to that
                new Thread(() -> {
                    try {
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                        ConnectionMessage c = (ConnectionMessage) in.readObject();

                        removeLobbiesUpdateListener(clientLobbyListener); // Ignore more lobby list updates
                        joinLobby(c.getLobbyName(), new SocketInfo(clientSocket, out, in), c.getPlayer());

                        // TODO: Timeout if we never read anything
                    } catch (IOException | ClassNotFoundException ignored) {
                        System.out.println("Client disconnected without joining any lobby");
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void joinLobby(String name, SocketInfo client, Player player) {
        synchronized (lobbies) {
            Lobby lobby = lobbies.get(name);
            if (lobby == null) {
                System.out.println("Creating lobby " + name);
                lobby = new Lobby() {
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
                    public void onGameStart() {
                        notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
                    }
                };
                lobbies.put(name, lobby);
            }
            lobby.connect(client, player);
            notifyLobbiesUpdate(new LobbiesUpdateMessage(makeLobbyInfo()));
            System.out.println("Player " + player.getName() + " has entered lobby " + name);
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
