package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerRemoteView extends View implements Runnable {
    Socket clientSocket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public ServerRemoteView(Socket clientSocket, Player player) {
        super(player);
        this.clientSocket = clientSocket;
    }

    @Override
    public void onBoardUpdate(BoardUpdateMessage message) {
        sendMessageViaSocket(message);
    }

    @Override
    public void onNextActionsUpdate(NextActionsUpdateMessage message) {
        sendMessageViaSocket(message);
    }

    @Override
    public void onStorageUpdate(StorageUpdateMessage message) {
        sendMessageViaSocket(message);
    }

    @Override
    public void onText(TextMessage message) {
        sendMessageViaSocket(message);
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
    public void run() {
        while (true) {
            try {
                if (in == null)
                    in = new ObjectInputStream(clientSocket.getInputStream());

                Message message = (Message) in.readObject();
                if (message instanceof MoveCommandMessage) {
                    notifyMoveCommand((MoveCommandMessage) message);
                } else if (message instanceof BuildCommandMessage) {
                    notifyBuildCommand((BuildCommandMessage) message);
                } else if (message instanceof EndTurnCommandMessage) {
                    notifyEndTurnCommand((EndTurnCommandMessage) message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
