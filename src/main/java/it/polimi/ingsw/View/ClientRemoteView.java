package it.polimi.ingsw.View;

import it.polimi.ingsw.View.Communication.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientRemoteView extends View implements Runnable {
    View wrapper;
    Socket clientSocket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public ClientRemoteView(Socket clientSocket, View wrapper) {
        super(wrapper.getPlayer());
        this.wrapper = wrapper;
        this.clientSocket = clientSocket;

        wrapper.addMoveCommandListener(this::sendMessageViaSocket);
        wrapper.addBuildCommandListener(this::sendMessageViaSocket);
        wrapper.addEndTurnCommandListener(this::sendMessageViaSocket);
    }

    private void sendMessageViaSocket(Message message) {
        try {
            if (out == null)
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.reset(); // Clear cache, otherwise mutable objects are not serialized again
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBoardUpdate(BoardUpdateMessage message) {
        wrapper.onBoardUpdate(message);
    }

    @Override
    public void onNextActionsUpdate(NextActionsUpdateMessage message) {
        wrapper.onNextActionsUpdate(message);
    }

    @Override
    public void onStorageUpdate(StorageUpdateMessage message) {
        wrapper.onStorageUpdate(message);
    }

    @Override
    public void onText(TextMessage message) {
        wrapper.onText(message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (in == null)
                    in = new ObjectInputStream(clientSocket.getInputStream());
                Message message = (Message) in.readObject();
                if (message instanceof BoardUpdateMessage) {
                    onBoardUpdate((BoardUpdateMessage) message);
                } else if (message instanceof StorageUpdateMessage) {
                    onStorageUpdate((StorageUpdateMessage) message);
                } else if (message instanceof NextActionsUpdateMessage) {
                    onNextActionsUpdate((NextActionsUpdateMessage) message);
                } else if (message instanceof TextMessage) {
                    onText((TextMessage) message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
