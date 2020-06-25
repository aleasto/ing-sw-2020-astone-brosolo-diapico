package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Communication.*;

import java.io.IOException;
import java.net.Socket;

public abstract class ClientRemoteView extends RemoteView {

    public ClientRemoteView(Player me) {
        super(me);
    }

    /**
     * Receives a message event from the network, and invokes its delegate method according to its instance type.
     *
     * @param message the message object
     */
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
        } else if (message instanceof PlayerLoseEventMessage) {
            onPlayerLoseEvent((PlayerLoseEventMessage) message);
        } else if (message instanceof LobbiesUpdateMessage) {
            onLobbiesUpdate((LobbiesUpdateMessage) message);
        } else if (message instanceof EndGameEventMessage) {
            onEndGameEvent((EndGameEventMessage) message);
        } else if (message instanceof PlayerChoseGodEventMessage) {
            onPlayerChoseGodEvent((PlayerChoseGodEventMessage) message);
        }
    }

    /**
     * Pipes all commands to the network.
     * The receiver, if listening, will receive an `onCommand` event
     *
     * @param message the command
     */
    @Override
    public void onCommand(CommandMessage message) {
        // Forward command to the network
        sendRemoteMessage(message);
    }


    /**
     * Connect this view to a tcp server.
     *
     * @param ip the host
     * @param port the port
     * @throws IOException if we fail to open the socket
     */
    public void connect(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
    }

    /**
     * Join a lobby on the server.
     * This sends a command just like onCommand() would do, but we'll keep it as a separate method cause it's fancier.
     *
     * @param lobby the lobby name
     */
    public void join(String lobby) {
        sendRemoteMessage(new JoinCommandMessage(getPlayer(), lobby));
    }
}
