package ru.deckbridge.compat;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import java.lang.reflect.Method;

public final class IronsSpellbooksCompat {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String MOD_ID = "irons_spellbooks";

    private static final String SPELL_WHEEL =
            "key.irons_spellbooks.spell_wheel";

    private static final String SPELLBOOK_CAST =
            "key.irons_spellbooks.spellbook_cast";

    private static final String SPELL_BAR_MODIFIER =
            "key.irons_spellbooks.spell_bar_modifier";

    private static final String QUICK_CAST_PREFIX =
            "key.irons_spellbooks.spell_quick_cast_";

    /*
     * Iron's handleInputEvent ожидает номер физической клавиши.
     *
     * Для DeckBridge он нам не нужен — состояние берётся
     * непосредственно из KeyMapping.isDown().
     *
     * Поэтому передаём значение, которое не совпадёт
     * с обычной клавиатурной кнопкой.
     */
    private static final int SYNTHETIC_KEY =
            Integer.MIN_VALUE + 100;

    private static boolean initializationAttempted = false;
    private static boolean available = false;

    private static Method handleInputEventMethod;

    private IronsSpellbooksCompat() {
    }

    public static void onPress(String actionName) {
        dispatch(actionName, InputConstants.PRESS);
    }

    public static void onRelease(String actionName) {
        dispatch(actionName, InputConstants.RELEASE);
    }

    public static boolean isSupportedAction(String actionName) {
        if (actionName == null) {
            return false;
        }

        if (SPELL_WHEEL.equals(actionName)) {
            return true;
        }

        if (SPELLBOOK_CAST.equals(actionName)) {
            return true;
        }

        if (SPELL_BAR_MODIFIER.equals(actionName)) {
            return true;
        }

        if (actionName.startsWith(QUICK_CAST_PREFIX)) {
            String numberText =
                    actionName.substring(QUICK_CAST_PREFIX.length());

            try {
                int number = Integer.parseInt(numberText);

                return number >= 1 && number <= 15;

            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        return false;
    }

    private static void dispatch(
            String actionName,
            int action
    ) {

        if (!isSupportedAction(actionName)) {
            return;
        }

        if (!ModList.get().isLoaded(MOD_ID)) {
            return;
        }

        ensureInitialized();

        if (!available || handleInputEventMethod == null) {
            return;
        }

        try {
            /*
             * К этому моменту DeckBridge уже выполнил:
             *
             * PRESS:
             * mapping.setDown(true)
             *
             * RELEASE:
             * mapping.setDown(false)
             *
             * Поэтому когда Iron's выполнит свой handleInputEvent(),
             * его KeyState увидит корректное состояние.
             */
            handleInputEventMethod.invoke(
                    null,
                    SYNTHETIC_KEY,
                    action
            );

            LOGGER.debug(
                    "DeckBridge Iron's Spells input: {} action={}",
                    actionName,
                    action
            );

        } catch (Throwable throwable) {
            LOGGER.error(
                    "DeckBridge failed to dispatch Iron's Spells input for {}",
                    actionName,
                    throwable
            );
        }
    }

    private static synchronized void ensureInitialized() {

        if (initializationAttempted) {
            return;
        }

        initializationAttempted = true;

        try {
            Class<?> clientInputEventsClass =
                    Class.forName(
                            "io.redspace.ironsspellbooks.player.ClientInputEvents"
                    );

            Method method =
                    clientInputEventsClass.getDeclaredMethod(
                            "handleInputEvent",
                            int.class,
                            int.class
                    );

            method.setAccessible(true);

            handleInputEventMethod = method;
            available = true;

            LOGGER.info(
                    "DeckBridge Iron's Spells 'n Spellbooks compatibility enabled"
            );

            LOGGER.info(
                    "DeckBridge Iron's Spells supported bindings: Spell Wheel, Cast Spell, Spell Bar Modifier, Quick Cast 1-15"
            );

        } catch (Throwable throwable) {

            available = false;

            LOGGER.error(
                    "DeckBridge detected Iron's Spells 'n Spellbooks, but compatibility initialization failed",
                    throwable
            );
        }
    }
}