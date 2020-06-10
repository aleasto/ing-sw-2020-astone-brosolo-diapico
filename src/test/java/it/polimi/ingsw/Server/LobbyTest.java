package it.polimi.ingsw.Server;

import it.polimi.ingsw.Game.GameRules;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.View.ServerRemoteView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    void connect() throws IOException {
        ConfReader conf = new ConfReader("server.conf");
        Lobby lobby = new Lobby(conf) {
            @Override
            public void closeLobby() {}
            @Override
            public void onPlayerLeave(Player p) {}
            @Override
            public void onSpectatorModeChanged(Player p, boolean spectator) {}
            @Override
            public void onGameStart(List<Player> players) {}
        };
        Player newPlayer = new Player("mario", 1);
        lobby.connect(new ServerRemoteView(new Socket()), newPlayer);
        assertEquals(1, lobby.getPlayerCount());
        assertEquals(0, lobby.getSpectatorCount());

        lobby.startGame(new GameRules());
        Player spec = new Player("spec", 2);
        lobby.connect(new ServerRemoteView(new Socket()), spec);
        assertEquals(1, lobby.getPlayerCount());
        assertEquals(1, lobby.getSpectatorCount());
    }

    @Test
    void closeLobby() throws IOException, InterruptedException {
        ConfReader conf = new ConfReader("server.conf");
        final boolean[] closed = {false};
        List<Player> playersThatLeft = new ArrayList<>();
        Lobby lobby = new Lobby(conf) {
            @Override
            public void closeLobby() {
                closed[0] = true;
            }
            @Override
            public void onPlayerLeave(Player p) {
                playersThatLeft.add(p);
            }
            @Override
            public void onSpectatorModeChanged(Player p, boolean spectator) {}
            @Override
            public void onGameStart(List<Player> players) {}
        };
        Player player = new Player("mario", 1);
        ServerRemoteView view = new ServerRemoteView(new Socket());
        view.startNetworkThread();
        lobby.connect(view, player);  // This will cause a network error which will disconnect the player
        Thread.sleep(100);  // Wait for network thread to notice the disconnection
        assertEquals(1, playersThatLeft.size());
        assertEquals(player, playersThatLeft.get(0));
        assertTrue(closed[0]);
    }

    @Test
    void closeLobbyByLosing() throws IOException, InterruptedException {
        ConfReader conf = new ConfReader("server.conf");
        final boolean[] closed = {false};
        Lobby lobby = new Lobby(conf) {
            @Override
            public void closeLobby() {
                closed[0] = true;
            }
            @Override
            public void onPlayerLeave(Player p) {}
            @Override
            public void onSpectatorModeChanged(Player p, boolean spectator) {}
            @Override
            public void onGameStart(List<Player> players) {}
        };
        Player player1 = new Player("mario", 999);
        ServerRemoteView view1 = new ServerRemoteView(new Socket());
        lobby.connect(view1, player1);
        Player player2 = new Player("mario", 1);
        ServerRemoteView view2 = new ServerRemoteView(new Socket());
        lobby.connect(view2, player2);
        lobby.startGame(new GameRules());
        assertDoesNotThrow(() -> lobby.getGame().EndTurn(player1, true));
        Thread.sleep(Lobby.END_GAME_TIMER + 1000);    // Wait for lobby to close
        assertTrue(closed[0]);
    }

    @Test
    void startGame() throws IOException {
        Lobby lobby = new Lobby(new ConfReader("server.conf")) {
            @Override
            public void closeLobby() {}
            @Override
            public void onPlayerLeave(Player p) {}
            @Override
            public void onSpectatorModeChanged(Player p, boolean spectator) {}
            @Override
            public void onGameStart(List<Player> players) {
                assertTrue(isGameInProgress());
            }
        };
        GameRules rules = new GameRules();
        lobby.startGame(rules);
        assertTrue(lobby.isGameInProgress());
    }
}