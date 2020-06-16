package it.polimi.ingsw.Game.Actions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
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
     *
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

    /**
     * Get all known god names
     *
     * @return a list of god names found in the loaded json
     */
    public static List<String> getGodNames() {
        if (cachedJson == null) {
            loadJsonOrExit();
        }

        return new ArrayList<>(cachedJson.keySet());
    }

    /**
     * Get infos for all known gods
     *
     * @return a list of GodInfo's for all gods found in the loaded json
     */
    public static List<GodInfo> getGodInfo() {
        if (cachedJson == null) {
            loadJsonOrExit();
        }

        return getGodNames().stream().map(god -> new GodInfo(god, cachedJson.getJSONObject(god).getString("description")))
                .collect(Collectors.toList());
    }

    /**
     * Get infos for a list of gods.
     * It discards unknown god names.
     *
     * @param godNames the god names
     * @return a list of god infos for the provided input names.
     */
    public static List<GodInfo> godInfosFor(List<String> godNames) {
        return getGodInfo().stream()
                .filter(i -> godNames.contains(i.getName())).collect(Collectors.toList());
    }

    /**
     * Get infos for a specific god
     * The input god name must be of a known god, else we throw an NPE
     *
     * @param godName the god name
     * @return the info object for this god
     */
    public static GodInfo godInfoFor(String godName) {
        return getGodInfo().stream().filter(i -> i.getName().equals(godName)).collect(Collectors.toList()).get(0);
    }

    /**
     * Load the default gods json, or exit the application if we fail reading it.
     */
    private static void loadJsonOrExit() {
        try {
            loadJson();
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Load a gods json from a custom input
     *
     * @param jsonStream the custom input stream
     * @throws JSONException if we fail to parse the json
     */
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

    /**
     * Loads the default gods json file, or generate it
     *
     * @throws IOException if we fail to generate the defaults
     * @throws JSONException if the default file is malformed
     */
    public static void loadJson() throws IOException, JSONException {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            System.out.println(CONFIG_FILE + " file not found. Generating defaults.");
            Files.copy(GodFactory.class.getResourceAsStream("/" + CONFIG_FILE), file.toPath());
        }
        InputStream configFileStream = new FileInputStream(file);
        loadJson(configFileStream);
    }

    /**
     * Decorate an Actions object with a decorator by its name
     * @param name the decorator's name
     * @param me the actions object to be decorated
     * @return a new actions object, which is the result of the decoration
     */
    // Use reflection to construct the decorator object by name
    private static Actions decorateWithClassName(String name, Actions me)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class c = Class.forName("it.polimi.ingsw.Game.Actions.Decorators." + name);
        Constructor constructor = c.getConstructor(new Class[]{Actions.class});
        Actions decoratedActions = (Actions) constructor.newInstance(me);
        return decoratedActions;
    }

    /**
     * Decorate an Actions object with an enemy decorator by its name
     * @param name the decorator's name
     * @param me the actions object to be decorated
     * @return a new actions object, which is the result of the decoration
     */
    private static Actions decorateEnemyWithClassName(String name, Actions enemy, Actions me)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class c = Class.forName("it.polimi.ingsw.Game.Actions.Decorators." + name);
        Constructor constructor = c.getConstructor(new Class[]{Actions.class, Actions.class});
        Actions decoratedActions = (Actions) constructor.newInstance(enemy, me);
        return decoratedActions;
    }

}
