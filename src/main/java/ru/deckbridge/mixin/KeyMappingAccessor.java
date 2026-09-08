package ru.deckbridge.mixin;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {

    @Accessor("clickCount")
    int deckbridge$getClickCount();

    @Accessor("clickCount")
    void deckbridge$setClickCount(int value);
}