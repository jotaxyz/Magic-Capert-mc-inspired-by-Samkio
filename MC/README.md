# MagicCarpet

Grief-free magic carpet plugin for Paper `1.21.1`, built with Java `21` and Maven.

Created by `jxtxdev`.

## Overview

MagicCarpet gives players a temporary flying carpet that follows them in real time without leaving blocks behind in the world. The plugin is designed to be safe for survival and public servers, with protection against grief, interaction, drops, and accidental world edits.

## Features

- Toggle the magic carpet with `/mc`
- Default `5x5` flying platform
- Carpet follows the player and does not remain in the map
- Smooth descent while sneaking
- Upward movement when jumping
- Temporary blocks are restored when the carpet moves or is disabled
- Protection against breaking, damaging, interacting with, or exploding carpet blocks
- Automatically removes the carpet when the player logs out
- Customizable through `config.yml`
- Permission-based access, disabled for default players by default

## Default Configuration

```yaml
carpet:
  size: 5
  edge-material: LIGHT_BLUE_STAINED_GLASS
  center-material: GLOWSTONE
  use-center-block: true

movement:
  descend-per-tick: 0.2
  ascend-per-jump: 1.0
  ascend-ticks-per-jump: 2

placement:
  only-air: true

messages:
  enabled: "&bMagic Carpet enabled."
  disabled: "&cMagic Carpet disabled."
  no-permission: "&cYou do not have permission to use this."
```

## Command

- `/mc` toggles the magic carpet for the player

## Permission

- `magiccarpet.use`
  Default: `false`

This makes the plugin ideal for VIP ranks, donors, or manually managed permission groups.

## Compatibility

- Minecraft: `1.21.1`
- Platform: `Paper`
- Java: `21`

## Build

```bash
mvn package
```

Compiled jar:

- `target/magiccarpet-1.0.0.jar`

## Open Source

This project is licensed under the MIT License. You are free to use, modify, and distribute it.

## Publishing Links

Recommended project links when publishing:

- Source code: GitHub repository
- Issues: GitHub issues page
- Downloads: GitHub Releases or Modrinth versions tab

## Roadmap Ideas

- Reload command for config
- Region protection hooks
- Per-player carpet styles
- Sound and particle customization
- Localization file support
