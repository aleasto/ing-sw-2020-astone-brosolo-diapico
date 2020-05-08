package it.polimi.ingsw.View;

import it.polimi.ingsw.View.Communication.*;

import java.net.Socket;

public class ClientRemoteView extends RemoteView {
    View wrapper;

    public ClientRemoteView(Socket clientSocket, View wrapper) {
        super(clientSocket, wrapper.getPlayer());
        this.wrapper = wrapper;

        wrapper.addCommandListener(this::sendRemoteMessage);
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
    public void onRemoteMessage(Message message) {
        if (message instanceof BoardUpdateMessage) {
            onBoardUpdate((BoardUpdateMessage) message);
        } else if (message instanceof StorageUpdateMessage) {
            onStorageUpdate((StorageUpdateMessage) message);
        } else if (message instanceof NextActionsUpdateMessage) {
            onNextActionsUpdate((NextActionsUpdateMessage) message);
        } else if (message instanceof TextMessage) {
            onText((TextMessage) message);
        }
    }

}
