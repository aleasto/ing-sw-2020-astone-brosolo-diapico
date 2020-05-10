package it.polimi.ingsw.Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {

    @Test
    void getOwner() {
        Player owner = new Player();
        Board board = new Board();
        Worker w = new Worker(owner, board.getAt(0, 0));
        assertEquals(owner, w.getOwner());
    }

    @Test
    void getTile() {
        Board board = new Board();
        Tile tile = board.getAt(0, 0);
        Worker w = new Worker(new Player(), tile);
        assertEquals(tile, w.getTile());
    }

    @Test
    void setTile() {
        Board board = new Board();
        Worker w = new Worker(new Player(), board.getAt(0, 0));
        Tile tile = board.getAt(1, 1);
        w.setTile(tile);
        assertEquals(tile, w.getTile());
    }

    @Test
    void canClone() throws CloneNotSupportedException {
        Board board = new Board();
        Worker w = new Worker(new Player(), board.getAt(0, 0));
        Tile tile = board.getAt(1, 1);
        w.setTile(tile);
        Worker w2 = w.clone();

        assertNotSame(w, w2);
        assertNull(w2.getTile());   // avoid deep-cloning recursively
        assertEquals(w.getOwner().getName(), w2.getOwner().getName());
    }
}
