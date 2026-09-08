package ru.deckbridge;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;

import java.util.Map;
import java.util.WeakHashMap;

public class D6ResetButtonRegistry {

    private static final Map<Button, KeyMapping> RESET_BUTTONS = new WeakHashMap<>();

    public static void register(Button button, KeyMapping keyMapping) {
        if (button == null || keyMapping == null) {
            return;
        }

        RESET_BUTTONS.put(button, keyMapping);
    }

    public static KeyMapping getKeyMapping(Button button) {
        if (button == null) {
            return null;
        }

        return RESET_BUTTONS.get(button);
    }
}