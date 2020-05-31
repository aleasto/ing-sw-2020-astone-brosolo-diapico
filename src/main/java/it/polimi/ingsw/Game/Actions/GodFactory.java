package it.polimi.ingsw.Game.Actions;

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
     * @param godPool a list of strings representing the god names in the pool
     * @return a list of the Actions in the same order as the input list
     */
    public static List<Actions> makeActions(List<String> godPool) {
        if (cachedJson == null)
            loadJsonOrExit();

        System.out.println("Making actions for a pool of: " + String.join(", ", godPool));

        // Build self actions for each god
        List<Actions> actionsList = new ArrayList<Actions>();
        for (String godName : godPool) {
            Actions decorated = new BaseActions();
            if (godName != null && !godName.isEmpty()) {
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
            }
            actionsList.add(decorated);
        }

        // And add malus to enemies

        // For each god
        for (int i = 0; i < godPool.size(); i++) {
            String myName = godPool.get(i);
            if (myName == null || myName.isEmpty()) continue;
            Actions me = actionsList.get(i);
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

        return actionsList;
    }

    public static List<String> getGodNames() {
        if (cachedJson == null) {
            loadJsonOrExit();
        }

        return new ArrayList<>(cachedJson.keySet());
    }

    public static List<GodInfo> getGodInfo() {
        if (cachedJson == null) {
            loadJsonOrExit();
        }

        return getGodNames().stream().map(god -> new GodInfo(god, cachedJson.getJSONObject(god).getString("description")))
                .collect(Collectors.toList());
    }

    private static void loadJsonOrExit() {
        try {
            loadJson();
        } catch (FileNotFoundException | JSONException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void loadJson(InputStream jsonStream) throws JSONException {
        cachedJson = new JSONObject(new JSONTokener(jsonStream));

        for (String god : getGodNames()) {
            try {
                JSONArray selfDecorators = cachedJson.getJSONObject(god).getJSONArray("self");
                for (int i = 0; i < selfDecorators.length(); i++) {
                    String className = selfDecorators.getString(i);
                    Class.forName("it.polimi.ingsw.Game.Actions.Decorators." + className);
                }
                JSONArray enemyDecorators = cachedJson.getJSONObject(god).getJSONArray("enemies");
                for (int i = 0; i < enemyDecorators.length(); i++) {
                    String className = enemyDecorators.getString(i);
                    Class.forName("it.polimi.ingsw.Game.Actions.Decorators." + className);
                }
            } catch (ClassNotFoundException e) {
                throw new JSONException("Invalid decorators for god " + god);
            }
        }

        getGodInfo();
    }

    public static void loadJson() throws FileNotFoundException, JSONException {
        InputStream configFileStream = new FileInputStream(CONFIG_FILE);
        loadJson(configFileStream);
    }

    // Use reflection to construct the decorator object by name
    private static Actions decorateWithClassName(String name, Actions me)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class c = Class.forName("it.polimi.ingsw.Game.Actions.Decorators." + name);
        Constructor constructor = c.getConstructor(new Class[]{Actions.class});
        Actions decoratedActions = (Actions) constructor.newInstance(me);
        return decoratedActions;
    }

    private static Actions decorateEnemyWithClassName(String name, Actions enemy, Actions me)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class c = Class.forName("it.polimi.ingsw.Game.Actions.Decorators." + name);
        Constructor constructor = c.getConstructor(new Class[]{Actions.class, Actions.class});
        Actions decoratedActions = (Actions) constructor.newInstance(enemy, me);
        return decoratedActions;
    }

}
