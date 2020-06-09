package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Utils.ConfReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void placeWorker() {
        GameRules rules = new GameRules();
        rules.setPlayWithGods(false);
        assertDoesNotThrow(() -> rules.fillDefaults(new ConfReader("server.conf")));
        List<Player> players = new ArrayList<>();
        players.add(new Player("it's-a-me", 999));
        players.add(new Player("mario", 1));
        Game game = new Game(players, rules);
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 0));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 1));
        assertFalse(game.getBoard().getAt(0, 0).isEmpty());
        assertEquals(players.get(0), game.getBoard().getAt(0, 0).getOccupant().getOwner());
    }

    @Test
    void move() {
        GameRules rules = new GameRules();
        rules.setPlayWithGods(false);
        assertDoesNotThrow(() -> rules.fillDefaults(new ConfReader("server.conf")));
        List<Player> players = new ArrayList<>();
        players.add(new Player("it's-a-me", 999));
        players.add(new Player("mario", 1));
        Game game = new Game(players, rules);
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 0));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 1));
        assertDoesNotThrow(() -> game.EndTurn(players.get(0), false));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(1), 1, 1));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(1), 1, 2));
        assertDoesNotThrow(() -> game.EndTurn(players.get(1), false));
        assertDoesNotThrow(() -> game.Move(players.get(0), 0, 0, 1, 0));
        assertFalse(game.getBoard().getAt(1, 0).isEmpty());
        assertEquals(players.get(0), game.getBoard().getAt(1, 0).getOccupant().getOwner());
    }

    @Test
    void build() {
        GameRules rules = new GameRules();
        rules.setPlayWithGods(false);
        assertDoesNotThrow(() -> rules.fillDefaults(new ConfReader("server.conf")));
        List<Player> players = new ArrayList<>();
        players.add(new Player("it's-a-me", 999));
        players.add(new Player("mario", 1));
        Game game = new Game(players, rules);
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 0));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 1));
        assertDoesNotThrow(() -> game.EndTurn(players.get(0), false));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(1), 1, 1));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(1), 1, 2));
        assertDoesNotThrow(() -> game.EndTurn(players.get(1), false));
        assertDoesNotThrow(() -> game.Move(players.get(0), 0, 0, 1, 0));
        assertDoesNotThrow(() -> game.Build(players.get(0), 1, 0, 0, 0, 0));
        assertEquals(1, game.getBoard().getAt(0, 0).getHeight());
    }

    @Test
    void endTurn() {
        GameRules rules = new GameRules();
        rules.setPlayWithGods(false);
        assertDoesNotThrow(() -> rules.fillDefaults(new ConfReader("server.conf")));
        List<Player> players = new ArrayList<>();
        players.add(new Player("it's-a-me", 999));
        players.add(new Player("mario", 1));
        Game game = new Game(players, rules);
        assertThrows(InvalidCommandException.class,
                () -> game.EndTurn(players.get(0), false));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 0));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 1));
        assertThrows(InvalidCommandException.class,
                () -> game.EndTurn(players.get(1), false));
        assertDoesNotThrow(() -> assertEquals(players.get(1), game.EndTurn(players.get(0), false)));

        List<Player> losingPlayers = new ArrayList<>();
        List<Player> winningPlayers = new ArrayList<>();
        game.addPlayerLoseEventListener(m -> losingPlayers.add(m.getPlayer()));
        game.addEndGameEventListener(m -> winningPlayers.add(m.getWinner()));
        assertDoesNotThrow(() -> game.EndTurn(players.get(1), true));

        assertEquals(1, winningPlayers.size());
        assertEquals(players.get(0), winningPlayers.get(0));
        assertEquals(1, losingPlayers.size());
        assertEquals(players.get(1), losingPlayers.get(0));
    }

    @Test
    void getWorkersOf() {
        GameRules rules = new GameRules();
        rules.setPlayWithGods(false);
        assertDoesNotThrow(() -> rules.fillDefaults(new ConfReader("server.conf")));
        List<Player> players = new ArrayList<>();
        players.add(new Player("it's-a-me", 999));
        players.add(new Player("mario", 1));
        Game game = new Game(players, rules);
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 0));
        assertDoesNotThrow(() -> game.PlaceWorker(players.get(0), 0, 1));
        List<Worker> workers = game.getWorkersOf(players.get(0));
        assertEquals(2, workers.size());
        assertTrue(workers.contains(game.getBoard().getAt(0, 0).getOccupant()));
        assertTrue(workers.contains(game.getBoard().getAt(0, 1).getOccupant()));
    }
}
