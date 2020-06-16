package it.polimi.ingsw.Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getName() {
        String name = "foo";
        Player player = new Player(name, 999);
        assertEquals(name, player.getName());
    }

    @Test
    void getGodName() {
        String godName = "bar";
        Player player = new Player("foo", 999);
        player.setGodName(godName);
        assertEquals(godName, player.getGodName());
    }

    @Test
    void getGodLikeLevel() {
        int lvl = 999;
        Player player = new Player("foo", lvl);
        assertEquals(lvl, player.getGodLikeLevel());
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        Player player = new Player("", 0);
        Player p2 = player.clone();
        assertNotSame(player, p2);
        assertEquals(p2, player);
    }
}
