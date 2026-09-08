package ru.deckbridge;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.slf4j.Logger;
import ru.deckbridge.mixin.KeyMappingAccessor;

public class D6InputDispatcher {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static String displayName(int id) {
        return "D6_BUTTON_" + id;
    }

    public static void press(int id) {
        if (tryAssignInControlsScreen(id)) {
            return;
        }

        if (tryCloseOpenedScreen(id)) {
            return;
        }

        triggerMappingsBoundTo(id);
    }

    public static void release(int id) {
        releaseMappingsBoundTo(id);
    }

    private static boolean tryAssignInControlsScreen(int id) {
        Minecraft mc = Minecraft.getInstance();

        if (!(mc.screen instanceof KeyBindsScreen screen)) {
            return false;
        }

        if (screen.selectedKey == null) {
            return false;
        }

        KeyMapping selected = screen.selectedKey;

        D6BindingStore.bindActionToD6Button(selected.getName(), id);

        screen.selectedKey = null;
        mc.options.save();

        LOGGER.info("Assigned {} to {}", selected.getName(), displayName(id));

        return true;
    }

    private static boolean tryCloseOpenedScreen(int id) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null) {
            return false;
        }

        if (mc.screen instanceof KeyBindsScreen) {
            return false;
        }

        boolean hasAnyBindingForButton = false;

        for (KeyMapping mapping : mc.options.keyMappings) {
            if (D6BindingStore.isActionBoundToButton(mapping.getName(), id)) {
                hasAnyBindingForButton = true;
                break;
            }
        }

        if (!hasAnyBindingForButton) {
            return false;
        }

        if (mc.player != null) {
            mc.player.closeContainer();
        }

        mc.setScreen(null);

        LOGGER.info("{} closed current screen", displayName(id));

        return true;
    }

    private static void triggerMappingsBoundTo(int id) {
        Minecraft mc = Minecraft.getInstance();

        for (KeyMapping mapping : mc.options.keyMappings) {
            String actionName = mapping.getName();

            if (D6BindingStore.isActionBoundToButton(actionName, id)) {
                /*
                 * clickCount нужен для действий, которые работают через consumeClick().
                 * setDown(true) нужен для модов, которые проверяют isDown().
                 */
                mapping.setDown(true);
                incrementClickCount(mapping);

                LOGGER.info("{} triggered mapping {}", displayName(id), actionName);
            }
        }
    }

    private static void releaseMappingsBoundTo(int id) {
        Minecraft mc = Minecraft.getInstance();

        for (KeyMapping mapping : mc.options.keyMappings) {
            String actionName = mapping.getName();

            if (D6BindingStore.isActionBoundToButton(actionName, id)) {
                mapping.setDown(false);

                LOGGER.info("{} released mapping {}", displayName(id), actionName);
            }
        }
    }

    private static void incrementClickCount(KeyMapping mapping) {
        try {
            KeyMappingAccessor accessor = (KeyMappingAccessor) mapping;
            int current = accessor.deckbridge$getClickCount();
            accessor.deckbridge$setClickCount(current + 1);
        } catch (Exception e) {
            LOGGER.error("Failed to increment clickCount for {}", mapping.getName(), e);
        }
    }
}