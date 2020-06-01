package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.NotConnectedException;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.Utils.Utils;
import it.polimi.ingsw.View.Communication.Message;
import it.polimi.ingsw.View.Communication.PingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RemoteView extends View {
    public static final int KEEP_ALIVE = 30 * 1000; // 30s
    public static final int ESTIMATED_MAX_NETWORK_DELAY = 5 * 1000; // 5s

    protected Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private final BlockingQueue<Message> outQueue;
    private Thread networkThread;

    public RemoteView(Player me) {
        super(me);
        this.outQueue = new LinkedBlockingQueue<>();
    }

    abstract public void onRemoteMessage(Message message);
    abstract public void onDisconnect();

    public void sendRemoteMessage(Message message) {
        try {
            outQueue.put(message);
        } catch (InterruptedException ignored) {}
    }

    public void startNetworkThread() {
        networkThread = new Thread(this::listen);
        networkThread.start();
    }

    public boolean isConnected() {
        return socket != null;
    }

    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                networkThread.join();
                socket = null;
            } catch (IOException | InterruptedException ignored) {}
        }
    }

    public void listen() throws NotConnectedException {
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
                if (out == null) {
                    out = new ObjectOutputStream(socket.getOutputStream());
                }
                while (true) {
                    out.writeObject(outQueue.take());
                }
            } catch (IOException | InterruptedException ignored) {}
        });
        outThread.start();

        // This thread for incoming packets
        try {
            if (in == null) {
                in = new ObjectInputStream(socket.getInputStream());
            }
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Object obj = in.readObject();
                    if (!(obj instanceof PingMessage)) {
                        onRemoteMessage((Message)obj);
                    } /* else avoid propagating upwards, as Ping is only needed to refresh SO_TIMEOUT */
                } catch (ClassNotFoundException | ClassCastException e) {
                    Log.logInvalidAction(getPlayer(), "", e.getMessage());
                }
            }
        } catch (IOException e) {
            Log.logInvalidAction(getPlayer(), "", e.getMessage());
            outThread.interrupt();
        }

        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException ignored) {}
        pingTimer.cancel();
        socket = null;
        out = null;
        in = null;

        new Thread(this::onDisconnect).start();
    }
}
