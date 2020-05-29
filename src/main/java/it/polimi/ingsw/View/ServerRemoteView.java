package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.Utils.SocketInfo;
import it.polimi.ingsw.View.Communication.*;

public abstract class ServerRemoteView extends RemoteView {

    public ServerRemoteView(SocketInfo client, Player me) {
        super(me);
        this.socket = client.getSocket();
        this.out = client.getOut();
        this.in = client.getIn();
        this.pingTimer = client.getPingTimer();
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
    public void onRemoteMessage(Message message) {
        if (message instanceof CommandMessage) {
            onCommand((CommandMessage) message);
        } else {
            Log.logInvalidAction(getPlayer(), "", "not a CommandMessage");
        }
    }
}
