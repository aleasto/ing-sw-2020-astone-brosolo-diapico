package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RemoteView extends View {
    BlockingQueue<Message> outQueue;
    Thread networkThread;

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

    public void startNetworkThread(Socket socket) {
        networkThread = new Thread(() -> listen(socket));
        networkThread.start();
    }

    public void stopNetworkThread() {
        networkThread.interrupt();
    }

    public void listen(Socket socket) {
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
