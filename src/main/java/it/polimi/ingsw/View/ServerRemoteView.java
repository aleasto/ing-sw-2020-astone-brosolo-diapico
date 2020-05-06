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
    public void onRemoteMessage(Message message) {
        if (message instanceof MoveCommandMessage) {
            notifyMoveCommand((MoveCommandMessage) message);
        } else if (message instanceof BuildCommandMessage) {
            notifyBuildCommand((BuildCommandMessage) message);
        } else if (message instanceof EndTurnCommandMessage) {
            notifyEndTurnCommand((EndTurnCommandMessage) message);
        }
    }
}
