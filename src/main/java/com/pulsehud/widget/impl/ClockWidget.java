package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockWidget extends Widget {
    private String cachedLocalTime = "";
    private String cachedGameTime = "";

    public ClockWidget() {
        super("clock", 120, 28);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showClock;
    }

    @Override
    public void updateData() {
        // 1. Fetch Local Computer Time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        cachedLocalTime = sdf.format(new Date());

        // 2. Fetch Minecraft Time of Day
        if (mc.world != null) {
            long timeOfDay = mc.world.getTimeOfDay();
            long day = (timeOfDay / 24000) + 1;
            long currentTick = timeOfDay % 24000;

            // Translate ticks into hours and minutes
            // 0 ticks = 6:00 AM (06:00)
            int hour = (int) (((currentTick / 1000) + 6) % 24);
            int minute = (int) ((currentTick % 1000) * 60 / 1000);
            
            String period = (hour >= 12) ? "PM" : "AM";
            int displayHour = hour % 12;
            if (displayHour == 0) displayHour = 12;

            String timePeriod;
            if (currentTick < 2000) {
                timePeriod = "Sunrise";
            } else if (currentTick < 5500) {
                timePeriod = "Morning";
            } else if (currentTick < 6500) {
                timePeriod = "Midday";
            } else if (currentTick < 11500) {
                timePeriod = "Afternoon";
            } else if (currentTick < 12500) {
                timePeriod = "Sunset";
            } else if (currentTick < 17500) {
                timePeriod = "Night";
            } else if (currentTick < 18500) {
                timePeriod = "Midnight";
            } else {
                timePeriod = "Late Night";
            }

            cachedGameTime = String.format("Day %d - %s", day, timePeriod);
        } else {
            cachedGameTime = "Day 1 - Morning";
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        
        drawGlassPanel(context, x, y, width, height);

        int primaryColor = ThemeManager.getPrimary();
        int textColor = ThemeManager.getText();

        // Line 1: Local Clock
        String localLabel = "Local: ";
        context.drawText(textRenderer, localLabel, x + 6, y + 4, primaryColor, false);
        context.drawText(textRenderer, cachedLocalTime, x + 6 + textRenderer.getWidth(localLabel), y + 4, textColor, false);

        // Line 2: Game Clock
        context.drawText(textRenderer, cachedGameTime, x + 6, y + 15, textColor, false);
    }
}
