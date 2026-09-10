# DeckBridge Input

DeckBridge Input is a client-side Minecraft Forge mod that turns FIFINE D6 and Stream Deck-style control-pad buttons into native Minecraft key bindings.

It adds virtual inputs named `D6_BUTTON_1` through `D6_BUTTON_999`. These virtual buttons can be assigned from Minecraft's normal **Options → Controls → Key Binds** screen and can trigger vanilla or modded actions without emulating keyboard shortcuts.

## Supported platform

- Minecraft Java Edition **1.19.2**
- Minecraft Forge **43.x**
- Java **17**
- Client-side only; do not install the mod on a dedicated server

## Features

- 999 independent virtual buttons: `D6_BUTTON_1` … `D6_BUTTON_999`
- Bind buttons through the normal Minecraft Controls menu
- Press and release events, including hold-style key bindings
- Works with vanilla and most modded `KeyMapping` controls
- Bindings persist across game restarts
- Local HTTP bridge bound only to `127.0.0.1:4567`
- Optional compatibility with **Controlling**
- Optional compatibility with **Iron's Spells 'n Spellbooks**, including Spell Wheel, Cast Spell, Spell Bar Modifier and Quick Cast 1–15

## Installation

1. Install Minecraft Forge for Minecraft 1.19.2.
2. Put the DeckBridge Input JAR into your client `mods` folder.
3. Start Minecraft and verify that **DeckBridge Input** appears in the Mods list.
4. Install/configure a compatible companion control-deck plugin or another local client that sends DeckBridge press/release requests.
5. Open **Options → Controls → Key Binds**.
6. Select the action you want to bind and press the configured D6/control-deck button.
7. Minecraft will show a binding such as `D6_BUTTON_17`.

## FIFINE D6 companion plugin

The companion FIFINE / StreamDock plugin is maintained separately:

https://github.com/ssss6610/com.deckbridge.minecraft.sdPlugin

Typical button settings:

```text
D6 Button ID: 17
Minecraft Host: 127.0.0.1
Minecraft Port: 4567
```

Each physical/control-deck button should use its own D6 Button ID.

## Local bridge

DeckBridge Input listens only on the loopback interface:

```text
http://127.0.0.1:4567
```

Health check:

```text
GET /d6/health
```

Press/release examples:

```text
GET /d6/press/17
GET /d6/release/17
```

No public network listener is created by the mod.

## Compatibility

DeckBridge is designed around Minecraft's `KeyMapping` system, so most vanilla and modded keybinds work without mod-specific integration.

Additional compatibility code is included for:

- Controlling (optional)
- Iron's Spells 'n Spellbooks (optional)

Neither mod is required to use DeckBridge Input.

## Building from source

```bash
./gradlew build
```

Windows:

```powershell
.\gradlew.bat build
```

The release JAR is written to `build/libs/`.

## Source and issues

Source: https://github.com/ssss6610/deckbridge-input

Issues: https://github.com/ssss6610/deckbridge-input/issues

## License

DeckBridge Input is released under the MIT License.
