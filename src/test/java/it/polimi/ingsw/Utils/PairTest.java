package it.polimi.ingsw.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void getFirst() {
        Object A = new Object();
        Object B = new Object();
        Pair<Object, Object> pair = new Pair<Object, Object>(A, B);
        assertEquals(A, pair.getFirst());
    }

    @Test
    void getSecond() {
        Object A = new Object();
        Object B = new Object();
        Pair<Object, Object> pair = new Pair<Object, Object>(A, B);
        assertEquals(B, pair.getSecond());
    }
}
