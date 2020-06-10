package it.polimi.ingsw.Game.Actions;

import it.polimi.ingsw.Game.Actions.Decorators.CanMoveTwice;
import it.polimi.ingsw.Game.Actions.Decorators.CanSwapWithEnemy;
import it.polimi.ingsw.Game.Actions.Decorators.CannotMoveUpIfEnemyDid;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GodFactoryTest {

    @Test
    void makeActions() {
        String customJson = "{" +
                "'Foo': { 'self': [CanMoveTwice], 'enemies': [], 'description': 'blah' }, " +
                "'Bar': { 'self': [CanSwapWithEnemy], 'enemies': [], 'description': 'blah' }, " +
                "'Baz': { 'self': [], 'enemies': [], 'description': 'blah' }, " +
                "'Qux': { 'self': [], 'enemies': [CannotMoveUpIfEnemyDid], 'description': 'blah' }" +
                "}";
        GodFactory.loadJson(new ByteArrayInputStream(customJson.getBytes()));

        List<String> godNames = Arrays.asList("Foo", "Bar", "Baz", "", null);
        List<Actions> actions = GodFactory.makeActions(godNames);
        assertEquals(CanMoveTwice.class, actions.get(godNames.indexOf("Foo")).getClass());
        assertEquals(CanSwapWithEnemy.class, actions.get(godNames.indexOf("Bar")).getClass());
        assertEquals(BaseActions.class, actions.get(godNames.indexOf("Baz")).getClass());
        assertEquals(BaseActions.class, actions.get(godNames.indexOf(null)).getClass());
        assertEquals(BaseActions.class, actions.get(godNames.indexOf("")).getClass());

        godNames = Arrays.asList("Qux", "Baz", null);
        actions = GodFactory.makeActions(godNames);
        assertEquals(BaseActions.class, actions.get(godNames.indexOf("Qux")).getClass());
        assertEquals(CannotMoveUpIfEnemyDid.class, actions.get(godNames.indexOf("Baz")).getClass());

        assertDoesNotThrow(() -> GodFactory.loadJson()); // Reset default json
    }

    @Test
    void makeActionsDuplicate() {
        String customJson = "{" +
                "'Foo': { 'self': [CanMoveTwice], 'enemies': [], 'description': 'blah' }, " +
                "'Bar': { 'self': [CanSwapWithEnemy], 'enemies': [], 'description': 'blah' }" +
                "}";
        GodFactory.loadJson(new ByteArrayInputStream(customJson.getBytes()));

        List<String> godNames = Arrays.asList("Foo", "Foo", "Foo");
        List<Actions> actions = GodFactory.makeActions(godNames);
        assertEquals(3, actions.stream().distinct().count());

        assertDoesNotThrow(() -> GodFactory.loadJson()); // Reset default json
    }
}
