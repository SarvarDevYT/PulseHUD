package com.pulsehud.theme;

import com.pulsehud.config.PulseConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private static final Map<String, Theme> THEMES = new HashMap<>();
    private static Theme targetTheme;
    
    // Smooth transition fields
    private static int currentPrimary;
    private static int currentSecondary;
    private static int currentGlow;
    private static int currentBg;
    private static int currentText;
    
    private static final float TRANSITION_SPEED = 0.05f;
    private static float idleSaturation = 1.0f;

    static {
        // Default Neon: Cyan & Purple
        THEMES.put("neon", new Theme("Neon", 0xFF00E5FF, 0xFFBD00FF, 0xAA00E5FF, 0x4D05050A, 0xFFFFFFFF));
        // Vanilla+: Enhanced classic
        THEMES.put("vanilla", new Theme("Vanilla+", 0xFFFF2222, 0xFFFF7777, 0x88FF2222, 0x80100000, 0xFFE0E0E0));
        // Cyberpunk: Bright Yellow & Cyan
        THEMES.put("cyberpunk", new Theme("Cyberpunk", 0xFFFFF500, 0xFF00FFCC, 0xAAFFF500, 0x550B0813, 0xFFFFF500));
        // Minimal Dark: Monochrome transparent glass
        THEMES.put("minimal", new Theme("Minimal Dark", 0xFFFFFFFF, 0xFF555555, 0x33FFFFFF, 0x90050505, 0xFFDDDDDD));
        // RGB Reactive: Smooth rainbow shift (will animate primary/secondary colors directly in tick)
        THEMES.put("rgb", new Theme("RGB Reactive", 0xFFFF0000, 0xFF00FF00, 0xAAFF00FF, 0x4D000000, 0xFFFFFFFF));
        
        // Default startup target
        targetTheme = THEMES.get("neon");
        
        currentPrimary = targetTheme.getPrimaryColor();
        currentSecondary = targetTheme.getSecondaryColor();
        currentGlow = targetTheme.getGlowColor();
        currentBg = targetTheme.getPanelBgColor();
        currentText = targetTheme.getTextPrimaryColor();
    }

    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Theme selectedBase = THEMES.getOrDefault(PulseConfigManager.getConfig().theme, THEMES.get("neon"));
        
        Theme target = selectedBase;

        // Overrides for biome reactivity
        if (PulseConfigManager.getConfig().biomeReactivity && mc.player != null && mc.world != null) {
            BlockPos pos = mc.player.getBlockPos();
            RegistryEntry<Biome> biomeEntry = mc.world.getBiome(pos);
            if (biomeEntry.getKey().isPresent()) {
                String biomeId = biomeEntry.getKey().get().getValue().getPath().toLowerCase();
                
                if (biomeId.contains("forest") || biomeId.contains("jungle") || biomeId.contains("plains")) {
                    // Forest: Green
                    target = new Theme("Biome Forest", 0xFF00FF66, 0xFF00AA44, 0xAA00FF66, 0x4D020D04, 0xFFE6FFE6);
                } else if (biomeId.contains("desert") || biomeId.contains("savanna") || biomeId.contains("badlands")) {
                    // Desert: Orange
                    target = new Theme("Biome Desert", 0xFFFF9900, 0xFFFF4400, 0xAAFF9900, 0x4D0E0700, 0xFFFFF2E6);
                } else if (biomeId.contains("snow") || biomeId.contains("ice") || biomeId.contains("frozen") || biomeId.contains("taiga")) {
                    // Snow: Icy Blue
                    target = new Theme("Biome Snow", 0xFF88DDFF, 0xFF2288FF, 0xAA88DDFF, 0x4D000914, 0xFFECF9FF);
                } else if (biomeId.contains("nether") || biomeId.contains("crimson") || biomeId.contains("warped") || biomeId.contains("basalt")) {
                    // Nether: Hellfire Crimson/Red Glow
                    target = new Theme("Biome Nether", 0xFFFF2A2A, 0xFF800000, 0xAAFF2A2A, 0x661A0000, 0xFFFFEBEB);
                } else if (biomeId.contains("the_end") || biomeId.contains("end_")) {
                    // The End: End Void Purple
                    target = new Theme("Biome The End", 0xFFDF00FF, 0xFF660099, 0xAADF00FF, 0x550F001A, 0xFFFDEBFF);
                }
            }
        }

        // Overrides for RGB Reactive dynamic rainbow effect
        if ("rgb".equals(PulseConfigManager.getConfig().theme)) {
            double time = System.currentTimeMillis() / 2000.0;
            int r1 = (int) (Math.sin(time) * 127 + 128);
            int g1 = (int) (Math.sin(time + 2.0 * Math.PI / 3.0) * 127 + 128);
            int b1 = (int) (Math.sin(time + 4.0 * Math.PI / 3.0) * 127 + 128);
            
            int r2 = (int) (Math.sin(time + Math.PI) * 127 + 128);
            int g2 = (int) (Math.sin(time + 5.0 * Math.PI / 3.0) * 127 + 128);
            int b2 = (int) (Math.sin(time + Math.PI / 3.0) * 127 + 128);

            int primary = (0xFF << 24) | (r1 << 16) | (g1 << 8) | b1;
            int secondary = (0xFF << 24) | (r2 << 16) | (g2 << 8) | b2;
            int glow = (0x88 << 24) | (r1 << 16) | (g1 << 8) | b1;
            
            target = new Theme("RGB Reactive Animated", primary, secondary, glow, 0x4D000000, 0xFFFFFFFF);
        }

        targetTheme = target;

        // Smooth color interpolation
        currentPrimary = Theme.blend(currentPrimary, targetTheme.getPrimaryColor(), TRANSITION_SPEED);
        currentSecondary = Theme.blend(currentSecondary, targetTheme.getSecondaryColor(), TRANSITION_SPEED);
        currentGlow = Theme.blend(currentGlow, targetTheme.getGlowColor(), TRANSITION_SPEED);
        currentBg = Theme.blend(currentBg, targetTheme.getPanelBgColor(), TRANSITION_SPEED);
        currentText = Theme.blend(currentText, targetTheme.getTextPrimaryColor(), TRANSITION_SPEED);
    }

    public static Theme getActiveTheme() {
        return THEMES.getOrDefault(PulseConfigManager.getConfig().theme, THEMES.get("neon"));
    }

    public static void setIdleSaturation(float s) {
        idleSaturation = s;
    }

    public static int getPrimary() {
        return desaturate(currentPrimary, idleSaturation);
    }

    public static int getSecondary() {
        return desaturate(currentSecondary, idleSaturation);
    }

    public static int getGlow() {
        return desaturate(currentGlow, idleSaturation);
    }

    public static int getBg() {
        return desaturate(currentBg, idleSaturation);
    }

    public static int getText() {
        return desaturate(currentText, idleSaturation);
    }

    private static int desaturate(int color, float factor) {
        if (factor >= 1.0f) return color;
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int gray = (r * 77 + g * 151 + b * 28) >> 8; // luminance weights
        r = (int) (r * factor + gray * (1 - factor));
        g = (int) (g * factor + gray * (1 - factor));
        b = (int) (b * factor + gray * (1 - factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
