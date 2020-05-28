package it.polimi.ingsw.Server;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.SocketInfo;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Broadcasters.LobbiesUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.LobbiesUpdateListener;
import it.polimi.ingsw.View.RemoteView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

            // Keep connected until other end disconnects
            int period = RemoteView.KEEP_ALIVE - RemoteView.ESTIMATED_MAX_NETWORK_DELAY;
            Timer pingTimer = new Timer();
            pingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    trySendSync(out, new PingMessage());
                }
            }, period, period);
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
                    joinLobby(c.getLobbyName(), new SocketInfo(clientSocket, out, in, pingTimer), c.getPlayer());
                } catch (IOException e) {
                    try {
                        if (in != null)
                            in.close();
                        out.close();
                        clientSocket.close();
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

    private void trySendSync(ObjectOutputStream to, Message message) {
        try {
            to.writeObject(message);
        } catch (IOException ignored) {}
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
