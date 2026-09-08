package ru.deckbridge.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.deckbridge.D6BindingStore;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Inject(
            method = "setKey",
            at = @At("HEAD")
    )
    private void deckbridge$setKey(KeyMapping keyMapping, InputConstants.Key key, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.screen == null) {
            return;
        }

        D6BindingStore.unbindAction(keyMapping.getName());
    }
}