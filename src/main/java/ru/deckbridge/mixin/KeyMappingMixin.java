package ru.deckbridge.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.deckbridge.D6BindingStore;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {

    @Shadow
    public abstract String getName();

    @Inject(
            method = "getTranslatedKeyMessage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void deckbridge$getTranslatedKeyMessage(CallbackInfoReturnable<Component> cir) {
        Integer d6ButtonId = D6BindingStore.getD6ButtonForAction(this.getName());

        if (d6ButtonId != null) {
            cir.setReturnValue(Component.literal("D6_BUTTON_" + d6ButtonId));
        }
    }

    @Inject(
            method = "isUnbound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void deckbridge$isUnbound(CallbackInfoReturnable<Boolean> cir) {
        Integer d6ButtonId = D6BindingStore.getD6ButtonForAction(this.getName());

        if (d6ButtonId != null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "isDefault",
            at = @At("HEAD"),
            cancellable = true
    )
    private void deckbridge$isDefault(CallbackInfoReturnable<Boolean> cir) {
        Integer d6ButtonId = D6BindingStore.getD6ButtonForAction(this.getName());

        if (d6ButtonId != null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "same",
            at = @At("HEAD"),
            cancellable = true
    )
    private void deckbridge$same(KeyMapping other, CallbackInfoReturnable<Boolean> cir) {
        Integer thisD6ButtonId = D6BindingStore.getD6ButtonForAction(this.getName());
        Integer otherD6ButtonId = D6BindingStore.getD6ButtonForAction(other.getName());

        boolean thisIsD6 = thisD6ButtonId != null;
        boolean otherIsD6 = otherD6ButtonId != null;

        if (thisIsD6 || otherIsD6) {
            /*
             * D6-кнопки конфликтуют только между собой,
             * если у них одинаковый ID.
             */
            cir.setReturnValue(
                    thisIsD6 &&
                            otherIsD6 &&
                            thisD6ButtonId.intValue() == otherD6ButtonId.intValue()
            );
        }
    }
}