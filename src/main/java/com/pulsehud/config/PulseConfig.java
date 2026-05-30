package com.pulsehud.config;

import java.util.HashMap;
import java.util.Map;

public class PulseConfig {
    // Main HUD
    public boolean enabled = true;
    public String theme = "neon"; // neon, vanilla, cyberpunk, minimal, rgb
    public float hudScale = 1.0f;
    public float baseOpacity = 0.85f;
    public float idleOpacity = 0.35f;
    public float animationSpeed = 1.0f;
    public float glowIntensity = 1.0f;
    public boolean biomeReactivity = true;

    // Elements toggles
    public boolean showArmor = true;
    public boolean showHealth = true;
    public boolean showHunger = true;
    public boolean showXp = true;
    public boolean showHotbar = true;

    // Dynamic states
    public boolean combatMode = true;
    public boolean idleMode = true;
    public boolean lowHealthVignette = true;
    public boolean speedPulse = true;
    public boolean waterOverlay = true;

    // Widgets toggles
    public boolean showFPS = true;
    public boolean showPing = true;
    public boolean showCoords = true;
    public boolean showCompass = true;
    public boolean showClock = true;
    public boolean showStats = true;
    public boolean showRTC = true;
    public boolean showSelectedItem = true;
    public boolean showPotionEffects = true;
    public boolean showBiome = true;
    public boolean showHitMarker = true;
    public float widgetOpacity = 0.5f;

    // Edit mode (drag-and-drop widgets)
    public boolean editMode = false;

    // Settings flag to trigger position reset on next save
    public boolean resetPositionsOnSave = false;

    // Position Offsets (X, Y)
    public Map<String, int[]> positions = new HashMap<>();

    public void resetDefaultPositions() {
        positions.clear();
        // [offsetX, offsetY, anchor, width, height]
        positions.put("health", new int[]{ -122, -60, 4, 110, 30 });
        positions.put("armor", new int[]{ -122, -90, 4, 24, 78 });
        positions.put("hunger", new int[]{ 12, -60, 4, 110, 30 });
        positions.put("xp", new int[]{ -91, -28, 4, 182, 4 });
        positions.put("hotbar", new int[]{ -91, -23, 4, 182, 22 });
        positions.put("fps", new int[]{ 10, 10, 0, 65, 16 });
        positions.put("ping", new int[]{ 10, 25, 0, 75, 16 });
        positions.put("coords", new int[]{ 10, 40, 0, 160, 16 });
        positions.put("compass", new int[]{ 0, 10, 5, 160, 20 });
        positions.put("clock", new int[]{ -10, 10, 1, 120, 28 });
        positions.put("stats", new int[]{ -10, 25, 1, 120, 52 });
        positions.put("rtc", new int[]{ -10, 45, 1, 120, 16 });
        positions.put("selecteditem", new int[]{ 0, 30, 5, 110, 28 });
        positions.put("potioneffects", new int[]{ 10, 60, 0, 130, 80 });
        positions.put("biome", new int[]{ 10, 55, 0, 130, 16 });
    }

    public PulseConfig() {
        resetDefaultPositions();
    }
}
