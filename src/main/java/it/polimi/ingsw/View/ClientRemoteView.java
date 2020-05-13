package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.View.Communication.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ClientRemoteView extends RemoteView {

    public ClientRemoteView(Player me) {
        super(me);
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
        } else if (message instanceof PlayersUpdateMessage) {
            onPlayersUpdate((PlayersUpdateMessage) message);
        } else if (message instanceof GodListMessage) {
            onShowGods((GodListMessage) message);
        } else if (message instanceof PlayerTurnUpdateMessage) {
            onPlayerTurnUpdate((PlayerTurnUpdateMessage) message);
        }
    }

    @Override
    public void onCommand(CommandMessage message) {
        // Forward command to the network
        sendRemoteMessage(message);
    }

    public Socket connect(String ip, String lobby) throws IOException {
        Socket socket = new Socket(ip, Server.PORT_NUMBER);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(new ConnectionMessage(getPlayer(), lobby));
        return socket;
    }
}
