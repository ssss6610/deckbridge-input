package ru.deckbridge;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class D6KeyMappings {

    /**
     * Максимальный ID виртуальной кнопки.
     *
     * FIFINE:
     * ID 1   -> D6_BUTTON_1
     * ID 2   -> D6_BUTTON_2
     * ...
     * ID 999 -> D6_BUTTON_999
     */
    public static final int MAX_KEYS = 999;

    /**
     * Мы НЕ регистрируем D6_BUTTON_1...999 как отдельные Minecraft keybinds,
     * чтобы не засорять меню управления.
     *
     * Назначение происходит через D6InputDispatcher:
     * выбранное действие -> D6_BUTTON_X.
     */
    public static void register(RegisterKeyMappingsEvent event) {
        // intentionally empty
    }
}