package ru.deckbridge.compat;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.slf4j.Logger;
import ru.deckbridge.D6BindingStore;

public final class ControllingCompat {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean registered = false;

    private ControllingCompat() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        try {
            Class<?> rawEventClass = Class.forName(
                    "com.blamejared.controlling.api.events.KeyEntryMouseClickedEvent"
            );

            if (!Event.class.isAssignableFrom(rawEventClass)) {
                LOGGER.error(
                        "DeckBridge: Controlling KeyEntryMouseClickedEvent is not a Forge Event"
                );
                return;
            }

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass =
                    (Class<? extends Event>) rawEventClass;

            registerListener(eventClass);

            registered = true;

            LOGGER.info(
                    "DeckBridge Controlling compatibility enabled (reflection mode)"
            );

        } catch (ClassNotFoundException e) {
            LOGGER.warn(
                    "DeckBridge: Controlling detected, but compatibility event class was not found"
            );

        } catch (Throwable throwable) {
            LOGGER.error(
                    "DeckBridge: Failed to initialize Controlling compatibility",
                    throwable
            );
        }
    }

    private static <T extends Event> void registerListener(Class<T> eventClass) {
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                eventClass,
                event -> handleEvent(event)
        );
    }

    private static void handleEvent(Event event) {
        try {
            Class<?> eventClass = event.getClass();

            int buttonId = ((Number) eventClass
                    .getMethod("getButtonId")
                    .invoke(event))
                    .intValue();

            if (buttonId != 0) {
                return;
            }

            double mouseX = ((Number) eventClass
                    .getMethod("getMouseX")
                    .invoke(event))
                    .doubleValue();

            double mouseY = ((Number) eventClass
                    .getMethod("getMouseY")
                    .invoke(event))
                    .doubleValue();

            Object entry = eventClass
                    .getMethod("getEntry")
                    .invoke(event);

            if (entry == null) {
                return;
            }

            Button resetButton = (Button) entry
                    .getClass()
                    .getMethod("getBtnResetKeyBinding")
                    .invoke(entry);

            if (resetButton == null) {
                return;
            }

            boolean clickedReset = resetButton.mouseClicked(
                    mouseX,
                    mouseY,
                    buttonId
            );

            if (!clickedReset) {
                return;
            }

            KeyMapping keyMapping = (KeyMapping) entry
                    .getClass()
                    .getMethod("getKeybinding")
                    .invoke(entry);

            if (keyMapping == null) {
                return;
            }

            String actionName = keyMapping.getName();

            Integer d6ButtonId =
                    D6BindingStore.getD6ButtonForAction(actionName);

            if (d6ButtonId == null) {
                return;
            }

            D6BindingStore.unbindAction(actionName);

            Minecraft mc = Minecraft.getInstance();

            if (mc != null && mc.options != null) {
                InputConstants.Key defaultKey =
                        keyMapping.getDefaultKey();

                keyMapping.setToDefault();

                mc.options.setKey(
                        keyMapping,
                        defaultKey
                );

                KeyMapping.resetMapping();

                mc.options.save();

                LOGGER.info(
                        "DeckBridge Controlling reset: {} from D6_BUTTON_{} to {}",
                        actionName,
                        d6ButtonId,
                        defaultKey.getName()
                );
            }

            eventClass
                    .getMethod("setHandled", boolean.class)
                    .invoke(event, true);

        } catch (Throwable throwable) {
            LOGGER.error(
                    "DeckBridge: Error while handling Controlling keybind event",
                    throwable
            );
        }
    }
}