package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.NotConnectedException;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RemoteView extends View {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            while (true) {
                Message message = (Message) in.readObject();
                onRemoteMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            outThread.interrupt();
        }

        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {}
        socket = null;
        out = null;
        in = null;

        new Thread(this::onDisconnect).start();
    }
}
