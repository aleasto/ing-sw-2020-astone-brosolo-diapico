package it.polimi.ingsw.Server;

import it.polimi.ingsw.View.Communication.ConnectionMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    public static final int PORT_NUMBER = 1234;

    public static void main(String[] args) {
        System.out.println("Server started!");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Lobby> lobbies = new HashMap();
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nNew client connected");
                // Read lobby name and connect to that
                new Thread(() -> {
                    try {
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                        ConnectionMessage c = (ConnectionMessage) in.readObject();
                        synchronized (lobbies) {
                            Lobby lobby = lobbies.get(c.getLobbyName());
                            if (lobby == null) {
                                lobby = new Lobby();
                                lobbies.put(c.getLobbyName(), lobby);
                            }
                            lobby.connect(clientSocket, c.getPlayer());
                            System.out.println("Player " + c.getPlayer().getName() + " has entered lobby " + c.getLobbyName());

                            // TODO: Timeout if we never read anything
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
