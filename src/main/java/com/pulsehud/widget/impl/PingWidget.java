package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingWidget extends Widget {
    private int cachedPing = 0;

    public PingWidget() {
        super("ping", 75, 16);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showPing;
    }

    @Override
    public void updateData() {
        if (mc.player != null && mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) {
                cachedPing = entry.getLatency();
            } else {
                cachedPing = 0;
            }
        } else {
            cachedPing = 0;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        
        drawGlassPanel(context, x, y, width, height);
        
        String text = "MS: " + cachedPing + " ms";
        int textWidth = textRenderer.getWidth(text);
        int drawX = x + (width - textWidth) / 2;
        int drawY = y + (height - 8) / 2;

        context.drawText(textRenderer, text, drawX, drawY, ThemeManager.getText(), false);
    }
}
