package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.NotConnectedException;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.Utils.Utils;
import it.polimi.ingsw.View.Communication.Message;
import it.polimi.ingsw.View.Communication.PingMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RemoteView extends View {
    public static final int KEEP_ALIVE = 30 * 1000; // 30s
    public static final int ESTIMATED_MAX_NETWORK_DELAY = 5 * 1000; // 5s

    protected Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final BlockingQueue<Message> outQueue;
    private Thread networkThread;

    public RemoteView(Player me) {
        super(me);
        this.outQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Event that fires when this view received a message from the network
     * @param message the message
     */
    abstract public void onRemoteMessage(Message message);

    /**
     * Event that fires when this view disconnected.
     * It may happen by a manual disconnection from either ends or by a timeout
     */
    abstract public void onDisconnect();

    /**
     * Send a message on the network
     * @param message the message
     */
    public void sendRemoteMessage(Message message) {
        try {
            outQueue.put(message);
        } catch (InterruptedException ignored) {}
    }

    /**
     * Asynchronously begin sending messages and listening for incoming messages on the network
     */
    public void startNetworkThread() {
        networkThread = new Thread(this::listen);
        networkThread.start();
    }

    /**
     * Manually disconnect this remote view
     */
    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                if (networkThread != null) {
                    networkThread.join();
                }
                socket = null;
            } catch (IOException | InterruptedException ignored) {}
        }
    }

    /**
     * Synchronously begin sending messages and listening for incoming messages on the network
     * @throws NotConnectedException if not connected
     */
    private void listen() throws NotConnectedException {
        if (socket == null) {
            throw new NotConnectedException("You are not connected");
        }

        // Keep connected until other end disconnects
        Timer pingTimer = Utils.makeTimer(() -> sendRemoteMessage(new PingMessage()), KEEP_ALIVE - ESTIMATED_MAX_NETWORK_DELAY);
        try {
            socket.setSoTimeout(KEEP_ALIVE);
        } catch (SocketException ignored) {}

        // A different thread for outgoing packets
        Thread outThread = new Thread(() -> {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    out.writeObject(outQueue.take());
                }
            } catch (IOException | InterruptedException ignored) {}
        });
        outThread.start();

        // This thread for incoming packets
        try {
            in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    Object obj = in.readObject();
                    if (!(obj instanceof PingMessage)) {
                        onRemoteMessage((Message)obj);
                    } /* else avoid propagating upwards, as Ping is only needed to refresh SO_TIMEOUT */
                } catch (ClassNotFoundException | ClassCastException | InvalidClassException | OptionalDataException e) {
                    Log.logInvalidAction(getPlayer(), "unknown", e.getMessage());
                }
            }
        } catch (IOException e) {
            outThread.interrupt();
        }

        try {
            socket.close();
            out.close();
            in.close();
        } catch (NullPointerException | IOException ignored) {}
        pingTimer.cancel();
        socket = null;
        out = null;
        in = null;

        new Thread(this::onDisconnect).start();
    }
}
