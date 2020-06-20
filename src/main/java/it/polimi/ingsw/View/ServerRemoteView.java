package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.View.Communication.*;

import java.net.Socket;

public class ServerRemoteView extends RemoteView {

    // We want to share this view between the server and the lobby, so we need different listeners at runtime
    private CommandListener commandListener = null;
    private DisconnectListener disconnectListener = null;

    /**
     * Create a server side remote view.
     * Server side means we already have a connected socket to use.
     * Also means we'll receive Commands and send Update/Event messages
     * @param client the client's socket
     */
    public ServerRemoteView(Socket client) {
        super(null);
        this.socket = client;
    }

    /**
     * Set the player associated with this view.
     * We do not have access to a player object until the clients sends us one,
     * which means we cannot directly initialize the view with a player but must do it later on
     * @param player the player object
     */
    public void setPlayer(Player player) {
        me = player;
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
    public void onPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
            sendRemoteMessage(message);
    }

    @Override
    public void onPlayerLoseEvent(PlayerLoseEventMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onEndGameEvent(EndGameEventMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onPlayerChoseGodEvent(PlayerChoseGodEventMessage message) {
        sendRemoteMessage(message);
    }

    @Override
    public void onLobbiesUpdate(LobbiesUpdateMessage message) {
        sendRemoteMessage(message);
    }

    /**
     * Event that fires when this view received a message from the network.
     * We will only accept Command messages
     * @param message the message
     */
    @Override
    public void onRemoteMessage(Message message) {
        if (message instanceof CommandMessage) {
            onCommand((CommandMessage) message);
        } else {
            Log.logInvalidAction(getPlayer(), "", "not a CommandMessage");
        }
    }

    @Override
    public void onCommand(CommandMessage message) {
        if (commandListener != null)
            commandListener.handle(message);
    }

    @Override
    public void onDisconnect() {
        if (disconnectListener != null)
            disconnectListener.handle();
    }

    /**
     * Set a command listener at runtime
     * @param listener the listener
     */
    public void setCommandListener(CommandListener listener) {
        this.commandListener = listener;
    }

    /**
     * Set a disconnection listener at runtime
     * @param disconnectListener the listener
     */
    public void setDisconnectListener(DisconnectListener disconnectListener) {
        this.disconnectListener = disconnectListener;
    }
}
