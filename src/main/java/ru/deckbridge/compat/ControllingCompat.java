package ru.deckbridge.compat;

import com.blamejared.controlling.api.events.KeyEntryMouseClickedEvent;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import ru.deckbridge.D6BindingStore;

public class ControllingCompat {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onKeyEntryMouseClicked(KeyEntryMouseClickedEvent event) {
        if (event.getButtonId() != 0) {
            return;
        }

        NewKeyBindsList.KeyEntry entry = event.getEntry();

        Button resetButton = entry.getBtnResetKeyBinding();

        boolean clickedReset = resetButton.mouseClicked(
                event.getMouseX(),
                event.getMouseY(),
                event.getButtonId()
        );

        if (!clickedReset) {
            return;
        }

        KeyMapping keyMapping = entry.getKeybinding();

        String actionName = keyMapping.getName();
        Integer d6ButtonId = D6BindingStore.getD6ButtonForAction(actionName);

        if (d6ButtonId == null) {
            return;
        }

        D6BindingStore.unbindAction(actionName);

        Minecraft mc = Minecraft.getInstance();

        if (mc != null && mc.options != null) {
            InputConstants.Key defaultKey = keyMapping.getDefaultKey();

            keyMapping.setToDefault();
            mc.options.setKey(keyMapping, defaultKey);
            KeyMapping.resetMapping();
            mc.options.save();

            LOGGER.info(
                    "DeckBridge Controlling reset: {} from D6_BUTTON_{} to {}",
                    actionName,
                    d6ButtonId,
                    defaultKey.getName()
            );
        }

        event.setHandled(true);
    }
}