# CurseForge release sheet — DeckBridge Input 1.0.0

This file contains the prepared values for the first CurseForge release.

## Project fields

**Game:** Minecraft

**Project name:** DeckBridge Input

**Class:** Mods

**Summary:**

> Use FIFINE D6 and Stream Deck-style control pads as native Minecraft keybinds.

**Mod loader:** Forge

**Minecraft version:** 1.19.2

**Environment:** Client-side only

**License:** MIT

**Source:** https://github.com/ssss6610/deckbridge-input

**Issue tracker:** https://github.com/ssss6610/deckbridge-input/issues

**Recommended main category:** Utility & QoL (use Miscellaneous if that category is unavailable in the current CurseForge form)

**Experimental:** No

**Comments:** Enabled

## Project description

DeckBridge Input lets you use FIFINE D6 and Stream Deck-style control-pad buttons as native Minecraft key bindings.

Instead of emulating ordinary keyboard shortcuts, DeckBridge adds 999 independent virtual inputs — `D6_BUTTON_1` through `D6_BUTTON_999` — directly to Minecraft's key-binding system. Select any vanilla or modded action in **Options → Controls → Key Binds**, press a configured control-deck button, and that action is bound to the corresponding D6 virtual button.

### Features

- 999 independent virtual buttons
- Native Minecraft Controls-menu binding
- Press and release events for click and hold actions
- Persistent bindings across restarts
- Works with vanilla and most modded key mappings
- Optional Controlling compatibility
- Optional Iron's Spells 'n Spellbooks compatibility
- Local-only bridge on `127.0.0.1:4567`
- No dedicated-server installation required

### Requirements

- Minecraft Java Edition 1.19.2
- Forge 43.x
- Java 17

DeckBridge Input is a **client-side mod**. Do not install it on a dedicated server.

### FIFINE D6 companion plugin

For FIFINE D6 / StreamDock hardware, install the companion plugin from:

https://github.com/ssss6610/com.deckbridge.minecraft.sdPlugin

Configure each physical button with a unique D6 Button ID, then bind it normally in Minecraft Controls.

### Compatibility

DeckBridge works through Minecraft's standard `KeyMapping` system. Controlling and Iron's Spells 'n Spellbooks are optional; neither is required.

Source code and issue tracker are available on GitHub under the MIT License.

## First file upload

Build with:

```bash
./gradlew clean build
```

Windows:

```powershell
.\gradlew.bat clean build
```

Upload:

```text
build/libs/deckbridge-input-1.0.0-forge-1.19.2.jar
```

**Display name:** DeckBridge Input 1.0.0 [Forge 1.19.2]

**Release type:** Release

**Supported version:** Minecraft 1.19.2

**Mod loader tag:** Forge

**Changelog type:** Markdown

Use the contents of `CHANGELOG.md` as the changelog.

## Related projects

No Minecraft mod is required by DeckBridge Input other than Forge itself. Controlling and Iron's Spells 'n Spellbooks may be listed as optional relations if desired.

The FIFINE/StreamDock companion plugin is external companion software, not a required Minecraft mod dependency.

## Assets still needed in the CurseForge dashboard

CurseForge requires an original square PNG project logo at least 400×400 pixels. Upload this separately in the project form.

Recommended screenshots:

1. Minecraft Controls screen showing a `D6_BUTTON_17` binding.
2. FIFINE Control Deck with several DeckBridge buttons configured.
3. A gameplay example showing a modded action triggered from the control deck.
