# PulseHUD

A fully customizable Minecraft HUD mod for Fabric, supporting **1.20.1** and **1.21.11**.

## Features

- **Custom Health Bar** — animated, Poison/Wither colors, absorption overlay
- **Custom Hunger Bar** — animated, sprint chevrons, oxygen bar
- **Custom Armor Display** — 4 vertical slots with durability
- **Custom XP Bar** — gradient fill, level display
- **Custom Hotbar** — glassmorphic, animated selection
- **10 Corner Widgets** — FPS, Ping, Coords, Compass, Clock, Session Stats, Biome, Selected Item, Potion Effects, RTC
- **5 Themes** — Neon, Vanilla+, Cyberpunk, Minimal Dark, RGB Reactive
- **Biome Reactivity** — colors adapt to your environment
- **Dynamic States** — Combat Mode, Idle Mode, Low Health Vignette, Water Overlay
- **Hit Marker** — animated crosshair on hit
- **Drag & Drop Edit Mode** — reposition/resize every element (I key)
- **28 Config Options** — via ModMenu / Cloth Config

## Installation

1. Install [Fabric Loader](https://fabricmc.net/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Cloth Config API](https://modrinth.com/mod/cloth-config)
4. Install [ModMenu](https://modrinth.com/mod/modmenu)
5. Download PulseHUD `.jar` from Releases and put it in `mods/`

## Building

```bash
# Select version
switch-version.bat   # choose 1 (1.20.1) or 2 (1.21.11)
gradlew build
```

The built jar will be in `build/libs/`.

## License

MIT
