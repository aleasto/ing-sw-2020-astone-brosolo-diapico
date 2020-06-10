package it.polimi.ingsw.Game;

import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {
    @Test
    void fillDefaults() throws IOException {
        ConfReader conf = new ConfReader("rules.testconf");
        GameRules rules = new GameRules();
        rules.fillDefaults(conf);
        assertEquals((Integer) 2, rules.getWorkers());
        assertEquals(new Pair<>(5, 5), rules.getBoardSize());
        assertArrayEquals(new Integer[]{22, 18, 14, 14}, rules.getBlocks());
        assertTrue(rules.getPlayWithGods());
    }
}