package it.polimi.ingsw.Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getName() {
        String name = "foo";
        Player player = new Player(name, "bar", 999);
        assertEquals(name, player.getName());
    }

    @Test
    void getGodName() {
        String godName = "bar";
        Player player = new Player("foo", godName, 999);
        assertEquals(godName, player.getGodName());
    }

    @Test
    void getGodLikeLevel() {
        int lvl = 999;
        Player player = new Player("foo", "bar", lvl);
        assertEquals(lvl, player.getGodLikeLevel());
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        Player player = new Player();
        Player p2 = player.clone();
        assertNotSame(player, p2);
        assertEquals(p2, player);
    }
}
