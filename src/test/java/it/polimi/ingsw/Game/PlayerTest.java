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
    void testClone() {
        Player player = new Player();
        Player p2 = null;
        try {
            p2 = player.clone();
        } catch (CloneNotSupportedException e) {
            fail("Should not have thrown any exception");
        }
        assertEquals(p2.getGodLikeLevel(), player.getGodLikeLevel());
    }
}
