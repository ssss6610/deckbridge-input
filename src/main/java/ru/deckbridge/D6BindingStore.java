package ru.deckbridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class D6BindingStore {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, Integer> BINDINGS = new HashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) {
            return;
        }

        loaded = true;

        File file = getBindingsFile();

        if (!file.exists()) {
            LOGGER.info("DeckBridge bindings file does not exist yet: {}", file.getAbsolutePath());
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> loadedBindings = GSON.fromJson(reader, type);

            if (loadedBindings != null) {
                BINDINGS.clear();
                BINDINGS.putAll(loadedBindings);
            }

            LOGGER.info("DeckBridge loaded {} bindings", BINDINGS.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load DeckBridge bindings", e);
        }
    }

    public static void save() {
        try {
            File file = getBindingsFile();

            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(BINDINGS, writer);
            }

            LOGGER.info("DeckBridge saved {} bindings", BINDINGS.size());
        } catch (Exception e) {
            LOGGER.error("Failed to save DeckBridge bindings", e);
        }
    }

    public static void bindActionToD6Button(String actionName, int d6ButtonId) {
        load();

        /*
         * Важно:
         * Один D6_BUTTON_X не должен висеть сразу на нескольких действиях.
         * Иначе после Reset кажется, что сброс не сработал, потому что
         * D6_BUTTON_1 уже назначился на соседнюю строку.
         */
        unbindD6ButtonWithoutSave(d6ButtonId);

        BINDINGS.put(actionName, d6ButtonId);
        save();

        LOGGER.info("DeckBridge binding set: {} -> D6_BUTTON_{}", actionName, d6ButtonId);
    }

    public static void unbindAction(String actionName) {
        load();

        if (BINDINGS.remove(actionName) != null) {
            save();
            LOGGER.info("DeckBridge binding removed: {}", actionName);
        }
    }

    public static void unbindD6Button(int d6ButtonId) {
        load();

        boolean changed = unbindD6ButtonWithoutSave(d6ButtonId);

        if (changed) {
            save();
            LOGGER.info("DeckBridge removed all bindings for D6_BUTTON_{}", d6ButtonId);
        }
    }

    private static boolean unbindD6ButtonWithoutSave(int d6ButtonId) {
        boolean changed = false;

        Iterator<Map.Entry<String, Integer>> iterator = BINDINGS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();

            if (entry.getValue() != null && entry.getValue() == d6ButtonId) {
                LOGGER.info("DeckBridge binding removed by button id: {} -> D6_BUTTON_{}", entry.getKey(), d6ButtonId);
                iterator.remove();
                changed = true;
            }
        }

        return changed;
    }

    public static Integer getD6ButtonForAction(String actionName) {
        load();
        return BINDINGS.get(actionName);
    }

    public static boolean isActionBoundToButton(String actionName, int d6ButtonId) {
        Integer boundButton = getD6ButtonForAction(actionName);
        return boundButton != null && boundButton == d6ButtonId;
    }

    private static File getBindingsFile() {
        File gameDir = Minecraft.getInstance().gameDirectory;
        return new File(gameDir, "config/deckbridge-bindings.json");
    }
}