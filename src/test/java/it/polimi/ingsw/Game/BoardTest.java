package it.polimi.ingsw.Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void getAt() {
        Board board = new Board(5, 5);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertNotNull(board.getAt(i, j));
                assertEquals(Tile.class, board.getAt(i, j).getClass());
            }
        }

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> board.getAt(5, 5));
    }

    @Test
    void testClone() {
        Board board = new Board();
        Board b2 = null;
        try {
            b2 = board.clone();
        } catch (CloneNotSupportedException e) {
            fail("Should not have thrown any exception");
        }
        assertFalse(board.equals(b2));
        assertEquals(board.getAt(1,1), b2.getAt(1,1));
    }
}
