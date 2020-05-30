package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Listeners.LobbiesUpdateListener;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ClientRemoteView extends RemoteView implements LobbiesUpdateListener {

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

    @Override
    public void onCommand(CommandMessage message) {
        // Forward command to the network
        sendRemoteMessage(message);
    }

    public void connect(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);

        // Keep connected until other end disconnects
        Timer pingTimer = new Timer();
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendRemoteMessage(new PingMessage());
            }
        }, KEEP_ALIVE - ESTIMATED_MAX_NETWORK_DELAY, KEEP_ALIVE - ESTIMATED_MAX_NETWORK_DELAY);
        this.pingTimer = pingTimer;

        try {
            socket.setSoTimeout(KEEP_ALIVE);
        } catch (SocketException ignored) {}
    }

    public void join(String lobby) {
        sendRemoteMessage(new JoinMessage(getPlayer(), lobby));
    }
}
