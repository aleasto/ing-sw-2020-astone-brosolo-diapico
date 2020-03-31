package it.polimi.ingsw.Actions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GodFactory {
    private static final String CONFIG_FILE = "gods.json";
    private static JSONObject cachedJson;

    /**
     * Build an Actions object for each god in the pool.
     * @param godPool a list strings representing the god names in the pool
     * @return a map of the god name and its corresponding Actions
     */
    public static Map<String, Actions> makeActions(List<String> godPool) {
        if(cachedJson == null)
            loadJson();

        System.out.println("Making actions for a pool of: " +
                           godPool.stream().collect(Collectors.joining(", ")));

        // Build self actions for each god
        List<Actions> actionsList = new ArrayList<Actions>();
        for (String godName : godPool) {
            Actions decorated = new BaseActions();
            JSONArray selfDecorators = cachedJson.getJSONObject(godName).getJSONArray("self");
            for (int j = 0; j < selfDecorators.length(); j++) {
                String decoratorName = selfDecorators.getString(j);
                try {
                    System.out.println(String.format("Decorating %s with %s", godName, decoratorName));
                    decorated = decorateWithClassName(decoratorName, decorated);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            actionsList.add(decorated);
        }

        // And add malus to enemies

        // For each god
        for (int i = 0; i < godPool.size(); i++) {
            Actions me = actionsList.get(i);
            String myName = godPool.get(i);
            JSONArray enemyDecorators = cachedJson.getJSONObject(myName).getJSONArray("enemies");

            // For each decorator
            for (int j = 0; j < enemyDecorators.length(); j++) {
                String decoratorName = enemyDecorators.getString(j);

                // For each enemy god
                for (int k = 0; k < godPool.size(); k++) {
                    Actions enemy = actionsList.get(k);
                    String enemyName = godPool.get(k);
                    if (k == i) continue;

                    try {
                        System.out.println(String.format("Decorating %s with %s because of %s",
                                                         enemyName, decoratorName, myName));
                        enemy = decorateEnemyWithClassName(decoratorName, enemy, me);
                        actionsList.set(k, enemy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Map<String, Actions> outMap = new HashMap<String, Actions>();
        for (int i = 0; i < godPool.size(); i++) {
            outMap.put(godPool.get(i), actionsList.get(i));
        }
        return outMap;
    }

    public static void loadJson(InputStream jsonStream) {
        try {
            cachedJson = new JSONObject(new JSONTokener(jsonStream));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void loadJson() {
        try {
            InputStream configFileStream = new FileInputStream(CONFIG_FILE);
            loadJson(configFileStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Use reflection to construct the decorator object by name
    private static Actions decorateWithClassName(String name, Actions me)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class c = Class.forName("it.polimi.ingsw.Actions.Decorators." + name);
        Constructor constructor = c.getConstructor(new Class[]{Actions.class});
        Actions decoratedActions = (Actions) constructor.newInstance(me);
        return decoratedActions;
    }

    private static Actions decorateEnemyWithClassName(String name, Actions enemy, Actions me)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class c = Class.forName("it.polimi.ingsw.Actions.Decorators." + name);
        Constructor constructor = c.getConstructor(new Class[]{Actions.class, Actions.class});
        Actions decoratedActions = (Actions) constructor.newInstance(enemy, me);
        return decoratedActions;
    }

}
