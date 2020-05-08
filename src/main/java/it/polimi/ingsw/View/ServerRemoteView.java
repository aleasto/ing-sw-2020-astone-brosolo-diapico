package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.*;

import java.net.Socket;

public class ServerRemoteView extends RemoteView {

    public ServerRemoteView(Socket socket, Player me) {
        super(socket, me);
    }

    @Override
    public void onBoardUpdate(BoardUpdateMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onNextActionsUpdate(NextActionsUpdateMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onStorageUpdate(StorageUpdateMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onText(TextMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onPlayersUpdate(PlayersUpdateMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onShowGods(GodListMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onRemoteMessage(Message message) {
        if (message instanceof CommandMessage) {
            notifyCommand((CommandMessage) message);
        }
    }

}
