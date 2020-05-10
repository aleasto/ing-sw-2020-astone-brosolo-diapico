package it.polimi.ingsw.Game;

import it.polimi.ingsw.Game.Storage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @Test
    void retrieve() {
        Storage myStorage = new Storage();
        int lvl = 1;
        int availLvl1 = myStorage.getAvailable(lvl);
        myStorage.retrieve(lvl);
        assertEquals(availLvl1 - 1, myStorage.getAvailable(lvl));
    }

    @Test
    void retrieveSubZero() {
        Storage myStorage = new Storage();
        int lvl = 1;
        int availLvl1 = myStorage.getAvailable(lvl);
        for (int i = 0; i < availLvl1; i++) {
            myStorage.retrieve(lvl);
        }
        assertEquals(0, myStorage.getAvailable(lvl));
        assertFalse(myStorage.retrieve(lvl));
    }

    @Test
    void retrieveIsolation() {
        Storage myStorage = new Storage();
        int lvl1 = 0;
        int availLvl1 = myStorage.getAvailable(lvl1);
        myStorage.retrieve(1);
        myStorage.retrieve(2);
        myStorage.retrieve(3);
        assertEquals(availLvl1, myStorage.getAvailable(lvl1));
    }

    @Test
    void retrieveOutOfBounds() {
        Storage myStorage = new Storage();
        assertEquals(0, myStorage.getAvailable(4)); // No piece of lvl4 exists
        assertFalse(myStorage.retrieve(4)); // No piece of lvl4 exists
        assertEquals(0, myStorage.getAvailable(-1)); // No piece of lvl-1 exists
        assertFalse(myStorage.retrieve(-1)); // No piece of lvl-1 exists
    }

    @Test
    void canClone() throws CloneNotSupportedException {
        Storage main = new Storage();
        Storage secondary = main.clone();
        assertEquals(main.retrieve(0), secondary.retrieve(0));
        assertEquals(main.retrieve(1), secondary.retrieve(1));
        assertEquals(main.retrieve(2), secondary.retrieve(2));
        assertEquals(main.retrieve(3), secondary.retrieve(3));
    }
}
