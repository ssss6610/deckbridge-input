package ru.deckbridge;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class D6VirtualKeyManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<Integer, Boolean> keyStates = new ConcurrentHashMap<>();

    public static void press(int keyId) {
        if (!isValidKeyId(keyId)) {
            LOGGER.warn("D6 key id out of range: {}", keyId);
            return;
        }

        keyStates.put(keyId, true);
        D6InputDispatcher.press(keyId);
    }

    public static void release(int keyId) {
        if (!isValidKeyId(keyId)) {
            LOGGER.warn("D6 key id out of range: {}", keyId);
            return;
        }

        keyStates.put(keyId, false);
        D6InputDispatcher.release(keyId);
    }

    public static boolean isPressed(int keyId) {
        return keyStates.getOrDefault(keyId, false);
    }

    public static String getDisplayName(int keyId) {
        return "D6_BUTTON_" + keyId;
    }

    private static boolean isValidKeyId(int keyId) {
        return keyId >= 1 && keyId <= D6KeyMappings.MAX_KEYS;
    }
}