package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.integration.rtc.RTCIntegration;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class RTCWidget extends Widget {
    private List<RTCIntegration.ClockEntry> cachedEntries = new ArrayList<>();

    public RTCWidget() {
        super("rtc", 120, 16);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showRTC && RTCIntegration.isAvailable();
    }

    @Override
    public void updateData() {
        if (RTCIntegration.isAvailable()) {
            cachedEntries = RTCIntegration.getClocks();
            
            // Adjust widget height and width dynamically based on clocks count and label lengths
            if (!cachedEntries.isEmpty()) {
                int maxTextWidth = 80;
                for (RTCIntegration.ClockEntry entry : cachedEntries) {
                    String line = "O  " + entry.label + " " + entry.formattedTime;
                    maxTextWidth = Math.max(maxTextWidth, textRenderer.getWidth(line));
                }
                this.width = maxTextWidth + 14;
                this.height = 8 + cachedEntries.size() * 11;
            } else {
                this.width = 100;
                this.height = 16;
            }
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        
        if (!RTCIntegration.isAvailable() || cachedEntries == null) {
            String text = "RTC Loading...";
            context.drawText(textRenderer, text, x + 6, y + 4, ThemeManager.getText(), false);
            return;
        }

        // Draw glass panel
        drawGlassPanel(context, x, y, width, height);

        // Calculate max text width for alignment
        int maxTextWidth = 0;
        for (int i = 0; i < cachedEntries.size(); i++) {
            String line = cachedEntries.get(i).label + " " + cachedEntries.get(i).formattedTime;
            maxTextWidth = Math.max(maxTextWidth, textRenderer.getWidth(line));
        }

        // Render each clock entry
        for (int i = 0; i < cachedEntries.size(); i++) {
            RTCIntegration.ClockEntry entry = cachedEntries.get(i);
            String label = entry.label;
            String icon = ">>";
            int drawY = y + 4 + i * 12;
            int primaryColor = ThemeManager.getPrimary();
            int textColor = ThemeManager.getText();

            context.drawText(textRenderer, icon, x + 6, drawY, primaryColor, false);
            context.drawText(textRenderer, label, x + 14, drawY, primaryColor, false);

            int labelW = textRenderer.getWidth(label);
            context.drawText(textRenderer, entry.formattedTime, x + 14 + labelW, drawY, textColor, false);
        }
    }
}
