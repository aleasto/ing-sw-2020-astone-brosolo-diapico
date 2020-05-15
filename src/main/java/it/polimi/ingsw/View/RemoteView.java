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
    private BlockingQueue<Message> outQueue;
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

    public void stopNetworkThread() {
        networkThread.interrupt();
        try {
            networkThread.join();
        } catch (InterruptedException ignored) {}
    }

    public boolean isConnected() {
        return socket != null;
    }

    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException ignored) {}
        }
    }

    public void listen() throws NotConnectedException {
        if (socket == null) {
            throw new NotConnectedException("You are not connected");
        }

        // A different thread for outgoing packets
        Thread outThread = new Thread(() -> {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    out.writeObject(outQueue.take());
                }
            } catch (IOException | InterruptedException ignored) { }
        });
        outThread.start();

        // This thread for incoming packets
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Message message = (Message) in.readObject();
                onRemoteMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            outThread.interrupt();
        }

        onDisconnect();
    }
}
