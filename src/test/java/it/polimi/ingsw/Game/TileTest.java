package it.polimi.ingsw.Game;

import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void compare() {
        //Tests the equals method
        Tile t1 = new Tile(null, 1, 5);
        Tile t2 = new Tile(null, 2, 4);
        Tile t3 = new Tile(null, 1, 5);
        assertFalse(t1.equals(t2));
        assertTrue(t1.equals(t3));
    }

    @Test
    void stressBuild() {
        //Tests that you can't build over a dome
        Board board = new Board();
        Tile t1 = board.getAt(1, 1);
        t1.buildDome();
        assertFalse(t1.buildUp());

        //Tests that you can't build over the max height level and that a dome
        //automatically generated if you build over a win height
        Tile t2 = board.getAt(2, 2);
        for (int i = 0; i < Tile.getMaxHeight(); i++) {
            t2.buildUp();
        }
        assertEquals(Tile.getMaxHeight(), t2.getHeight());
        assertFalse(t2.hasDome());
        // We are allowed to call buildUp() once more to build a dome
        assertTrue(t2.buildUp());
        assertTrue(t2.hasDome());
        assertFalse(t2.buildUp());
    }

    @Test
    void canClone() {
        Board board = new Board();
        Tile t1 = board.getAt(1, 1);
        Worker w = new Worker(new Player(), t1);
        t1.setOccupant(w);
        Tile t2 = null;
        try {
            t2 = t1.clone();
        } catch (CloneNotSupportedException e) {
            fail("Should not have thrown any exception");
        }
        assertTrue(t1.equals(t2));
        assertFalse(t1 == t2);
        assertTrue(t1.getX() == t2.getX() && t1.getY() == t2.getY());
    }
}
