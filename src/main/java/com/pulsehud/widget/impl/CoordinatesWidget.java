package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class CoordinatesWidget extends Widget {
    private int cachedX = 0;
    private int cachedY = 0;
    private int cachedZ = 0;
    private String cachedDirection = "S";

    public CoordinatesWidget() {
        super("coords", 160, 16);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showCoords;
    }

    @Override
    public void updateData() {
        if (mc.player != null) {
            cachedX = (int) Math.floor(mc.player.getX());
            cachedY = (int) Math.floor(mc.player.getY());
            cachedZ = (int) Math.floor(mc.player.getZ());

            float yaw = mc.player.getYaw();
            float heading = (yaw % 360.0f);
            if (heading < 0) heading += 360.0f;

            if (heading >= 337.5f || heading < 22.5f) {
                cachedDirection = "S";
            } else if (heading >= 22.5f && heading < 67.5f) {
                cachedDirection = "SW";
            } else if (heading >= 67.5f && heading < 112.5f) {
                cachedDirection = "W";
            } else if (heading >= 112.5f && heading < 157.5f) {
                cachedDirection = "NW";
            } else if (heading >= 157.5f && heading < 202.5f) {
                cachedDirection = "N";
            } else if (heading >= 202.5f && heading < 247.5f) {
                cachedDirection = "NE";
            } else if (heading >= 247.5f && heading < 292.5f) {
                cachedDirection = "E";
            } else {
                cachedDirection = "SE";
            }
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        
        drawGlassPanel(context, x, y, width, height);

        String text = String.format("X: %d  Y: %d  Z: %d (%s)", cachedX, cachedY, cachedZ, cachedDirection);
        int textWidth = textRenderer.getWidth(text);
        int drawX = x + (width - textWidth) / 2;
        int drawY = y + (height - 8) / 2;

        context.drawText(textRenderer, text, drawX, drawY, ThemeManager.getText(), false);
    }
}
