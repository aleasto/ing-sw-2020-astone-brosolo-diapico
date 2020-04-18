package it.polimi.ingsw.Game.Actions;

import it.polimi.ingsw.Game.Actions.Decorators.CanMoveTwice;
import it.polimi.ingsw.Game.Actions.Decorators.CanSwapWithEnemy;
import it.polimi.ingsw.Game.Actions.Decorators.CannotMoveUpIfEnemyDid;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GodFactoryTest {

    @Test
    void makeActions() {
        String customJson = "{" +
                "'Foo': { 'self': [CanMoveTwice], 'enemies': [] }, " +
                "'Bar': { 'self': [CanSwapWithEnemy], 'enemies': [] }, " +
                "'Baz': { 'self': [], 'enemies': [] }, " +
                "'Qux': { 'self': [], 'enemies': [CannotMoveUpIfEnemyDid] }" +
                "}";
        GodFactory.loadJson(new ByteArrayInputStream(customJson.getBytes()));

        Map<String, Actions> actionsMap = GodFactory.makeActions(Arrays.asList("Foo", "Bar", "Baz"));
        assertEquals(CanMoveTwice.class, actionsMap.get("Foo").getClass());
        assertEquals(CanSwapWithEnemy.class, actionsMap.get("Bar").getClass());
        assertEquals(BaseActions.class, actionsMap.get("Baz").getClass());

        actionsMap = GodFactory.makeActions(Arrays.asList("Qux", "Baz"));
        assertEquals(BaseActions.class, actionsMap.get("Qux").getClass());
        assertEquals(CannotMoveUpIfEnemyDid.class, actionsMap.get("Baz").getClass());
    }
}
