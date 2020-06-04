package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.View.Communication.*;

import java.net.Socket;

public class ServerRemoteView extends RemoteView {

    public ServerRemoteView(Socket client) {
        super(null);
        this.socket = client;
    }

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

    @Override
    public void onRemoteMessage(Message message) {
        if (message instanceof CommandMessage) {
            onCommand((CommandMessage) message);
        } else {
            Log.logInvalidAction(getPlayer(), "", "not a CommandMessage");
        }
    }

    // We want to share this view between the server and the lobby, so we need different listeners at runtime
    private CommandListener commandListener = null;
    public void setCommandListener(CommandListener listener) {
        this.commandListener = listener;
    }
    @Override
    public void onCommand(CommandMessage message) {
        if (commandListener != null)
            commandListener.handle(message);
    }

    private DisconnectListener disconnectListener = null;
    public void setDisconnectListener(DisconnectListener disconnectListener) {
        this.disconnectListener = disconnectListener;
    }
    @Override
    public void onDisconnect() {
        if (disconnectListener != null)
            disconnectListener.handle();
    }
}
