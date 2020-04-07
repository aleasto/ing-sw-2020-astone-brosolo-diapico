package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void compare() {
        //Tests the equals method
        Tile t1 = new Tile(1, 5);
        Tile t2 = new Tile(2, 4);
        Tile t3 = new Tile(1, 5);
        assertFalse(t1.equals(t2));
        assertTrue(t1.equals(t3));
    }

    @Test
    void stressBuild() {
        //Tests that you can't build over a dome
        Tile t1 = new Tile(1, 1);
        t1.buildDome();
        assertFalse(t1.buildUp());

        //Tests that you can't build over the max height level and that a dome
        //automatically generated if you build over a win height
        Tile t2 = new Tile(2, 2);
        for (int i = 0; i <= t2.getMaxHeight(); i++) {
            t2.buildUp();
        }
        assertFalse(t2.buildUp());
        assertTrue(t2.hasDome());
    }
}
