package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RemoteView extends View implements Runnable {
    Socket socket;
    BlockingQueue<Message> outQueue;

    public RemoteView(Socket socket, Player me) {
        super(me);
        this.socket = socket;
        this.outQueue = new LinkedBlockingQueue<>();
    }

    abstract public void onRemoteMessage(Message message);

    public void sendRemoteMessage(Message message) {
        try {
            outQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // A different thread for outgoing packets
        Thread outThread = new Thread(() -> {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    out.reset();
                    out.writeObject(outQueue.take());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
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
            e.printStackTrace();
        }

        try {
            outThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
