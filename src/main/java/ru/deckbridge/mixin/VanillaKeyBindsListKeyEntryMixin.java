package ru.deckbridge.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.deckbridge.D6BindingStore;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class VanillaKeyBindsListKeyEntryMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    private KeyMapping key;

    @Shadow
    @Final
    private Button changeButton;

    @Shadow
    @Final
    private Button resetButton;

    @Inject(
            method = "mouseClicked(DDI)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void deckbridge$mouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if (mouseButton != 0) {
            return;
        }

        boolean clickedReset = this.resetButton.mouseClicked(mouseX, mouseY, mouseButton);

        if (!clickedReset) {
            return;
        }

        String actionName = this.key.getName();
        Integer d6ButtonId = D6BindingStore.getD6ButtonForAction(actionName);

        if (d6ButtonId == null) {
            return;
        }

        D6BindingStore.unbindAction(actionName);

        Minecraft mc = Minecraft.getInstance();

        if (mc != null && mc.options != null) {
            InputConstants.Key defaultKey = this.key.getDefaultKey();

            this.key.setToDefault();
            mc.options.setKey(this.key, defaultKey);
            KeyMapping.resetMapping();
            mc.options.save();

            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            this.resetButton.active = !this.key.isDefault();

            LOGGER.info(
                    "DeckBridge vanilla reset: {} from D6_BUTTON_{} to {}",
                    actionName,
                    d6ButtonId,
                    defaultKey.getName()
            );
        }

        cir.setReturnValue(true);
    }
}