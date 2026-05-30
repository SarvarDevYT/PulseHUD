package com.pulsehud.hud;

import com.pulsehud.config.PulseConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HitMarkerManager {
    private static long lastHitTime = 0;
    private static final long HIT_MARKER_DURATION_MS = 400;

    public static void registerHit() {
        lastHitTime = System.currentTimeMillis();
    }

    public static void render(DrawContext context) {
        if (!PulseConfigManager.getConfig().showHitMarker) return;
        long now = System.currentTimeMillis();
        if (now - lastHitTime > HIT_MARKER_DURATION_MS) return;

        float progress = (float) (now - lastHitTime) / HIT_MARKER_DURATION_MS;
        int alpha = (int) ((1.0f - progress) * 255);

        MinecraftClient mc = MinecraftClient.getInstance();
        int w = context.getScaledWindowWidth();
        int h = context.getScaledWindowHeight();
        int cx = w / 2;
        int cy = h / 2;

        int color = (alpha << 24) | 0xFFFFFF;
        int len = 6;
        int gap = 3;

        // Crosshair style: four lines around center
        context.fill(cx - gap - len, cy, cx - gap, cy + 1, color); // left
        context.fill(cx + gap + 1, cy, cx + gap + len + 1, cy + 1, color); // right
        context.fill(cx, cy - gap - len, cx + 1, cy - gap, color); // top
        context.fill(cx, cy + gap + 1, cx + 1, cy + gap + len + 1, color); // bottom
    }
}
