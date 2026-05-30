package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class SessionStatsWidget extends Widget {
    private static long sessionStartTime = 0;
    private static int mobsKilled = 0;
    private static int blocksMined = 0;
    private static double distanceTraveled = 0;

    private double lastX = 0;
    private double lastY = 0;
    private double lastZ = 0;
    private boolean hasLastPos = false;

    public SessionStatsWidget() {
        super("stats", 120, 52);
        if (sessionStartTime == 0) {
            sessionStartTime = System.currentTimeMillis();
        }
    }

    public static void incrementKills() {
        mobsKilled++;
    }

    public static void incrementBlocksMined() {
        blocksMined++;
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showStats;
    }

    @Override
    public void updateData() {
        if (mc.player != null) {
            double currentX = mc.player.getX();
            double currentY = mc.player.getY();
            double currentZ = mc.player.getZ();

            if (hasLastPos) {
                double dx = currentX - lastX;
                double dy = currentY - lastY;
                double dz = currentZ - lastZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                // Filter out teleportation or spawns (dist > 10 blocks in a single second)
                if (dist < 10) {
                    distanceTraveled += dist;
                }
            }

            lastX = currentX;
            lastY = currentY;
            lastZ = currentZ;
            hasLastPos = true;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        
        drawGlassPanel(context, x, y, width, height);

        int primaryColor = ThemeManager.getPrimary();
        int textColor = ThemeManager.getText();

        // 1. Time Elapsed
        long elapsedSec = (System.currentTimeMillis() - sessionStartTime) / 1000;
        long h = elapsedSec / 3600;
        long m = (elapsedSec % 3600) / 60;
        long s = elapsedSec % 60;
        String timeStr = String.format("%02d:%02d:%02d", h, m, s);

        context.drawText(textRenderer, "Session: " + timeStr, x + 6, y + 4, primaryColor, false);

        // 2. Mobs Killed & Blocks Mined
        String combatStr = "Kills: " + mobsKilled;
        String mineStr = "Mined: " + blocksMined;
        context.drawText(textRenderer, combatStr, x + 6, y + 16, textColor, false);
        context.drawText(textRenderer, mineStr, x + 6, y + 27, textColor, false);

        // 3. Distance Traveled
        String distStr;
        if (distanceTraveled >= 1000) {
            distStr = String.format("Dist: %.2f km", distanceTraveled / 1000.0);
        } else {
            distStr = String.format("Dist: %.0f m", distanceTraveled);
        }
        context.drawText(textRenderer, distStr, x + 6, y + 38, textColor, false);
    }
}
