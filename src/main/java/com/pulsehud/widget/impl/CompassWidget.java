package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class CompassWidget extends Widget {
    private float heading = 0.0f;

    public CompassWidget() {
        super("compass", 160, 20);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showCompass;
    }

    @Override
    public void updateData() {
        if (mc.player != null) {
            float yaw = mc.player.getYaw();
            heading = (yaw % 360.0f);
            if (heading < 0) heading += 360.0f;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        
        drawGlassPanel(context, x, y, width, height);

        // Smooth interpolation of yaw on each frame for fluid scrolling
        if (mc.player != null) {
            float yaw = mc.player.getYaw();
            float targetHeading = yaw % 360.0f;
            if (targetHeading < 0) targetHeading += 360.0f;

            // Handle wrap-around LERP smoothly
            float diff = targetHeading - heading;
            while (diff < -180.0f) diff += 360.0f;
            while (diff > 180.0f) diff -= 360.0f;
            heading += diff * 0.2f * tickDelta;
            if (heading < 0) heading += 360.0f;
            if (heading >= 360.0f) heading -= 360.0f;
        }

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        float fovDegrees = 90.0f; // Viewable degree range
        float pixelsPerDegree = (float) width / fovDegrees;

        // Render direction letters and tick marks
        String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        int[] degrees = {0, 45, 90, 135, 180, 225, 270, 315};

        for (int i = 0; i < degrees.length; i++) {
            float deg = degrees[i];
            float diff = deg - heading;
            while (diff < -180.0f) diff += 360.0f;
            while (diff > 180.0f) diff -= 360.0f;

            float drawX = centerX + (diff * pixelsPerDegree);

            // Render only if within visible box
            if (drawX >= x + 4 && drawX <= x + width - 4) {
                float distFromCenter = Math.abs(drawX - centerX);
                float opacity = 1.0f - (distFromCenter / (width / 2.0f));
                opacity = Math.max(0.0f, Math.min(1.0f, opacity));

                int baseColor = ThemeManager.getText();
                // Override N with Primary Theme Color (e.g. Neon Cyan) to pop!
                if ("N".equals(directions[i])) {
                    baseColor = ThemeManager.getPrimary();
                }
                
                int fadedColor = fadeColor(baseColor, opacity);

                String label = directions[i];
                int labelWidth = textRenderer.getWidth(label);
                
                // Draw label
                context.drawText(textRenderer, label, (int) (drawX - labelWidth / 2.0f), y + 2, fadedColor, false);
                // Draw a small tick mark under label
                context.fill((int) drawX - 1, y + 11, (int) drawX + 1, y + 15, fadedColor);
            }
        }

        // Draw sub-ticks every 15 degrees
        for (int deg = 0; deg < 360; deg += 15) {
            if (deg % 45 == 0) continue; // Skip if it already has a label

            float diff = deg - heading;
            while (diff < -180.0f) diff += 360.0f;
            while (diff > 180.0f) diff -= 360.0f;

            float drawX = centerX + (diff * pixelsPerDegree);

            if (drawX >= x + 4 && drawX <= x + width - 4) {
                float distFromCenter = Math.abs(drawX - centerX);
                float opacity = 1.0f - (distFromCenter / (width / 2.0f));
                opacity = Math.max(0.0f, Math.min(1.0f, opacity)) * 0.5f; // Dimmer ticks

                int fadedColor = fadeColor(ThemeManager.getSecondary(), opacity);
                context.fill((int) drawX, y + 12, (int) drawX + 1, y + 15, fadedColor);
            }
        }

        // Draw Center Indicator Arrow (Chevron)
        int primaryColor = ThemeManager.getPrimary();
        // Pointing down triangle
        context.fill(centerX - 2, y, centerX + 3, y + 1, primaryColor);
        context.fill(centerX - 1, y + 1, centerX + 2, y + 2, primaryColor);
        context.fill(centerX, y + 2, centerX + 1, y + 3, primaryColor);
    }

    private int fadeColor(int color, float opacity) {
        int a = (int) (((color >> 24) & 0xFF) * opacity);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
