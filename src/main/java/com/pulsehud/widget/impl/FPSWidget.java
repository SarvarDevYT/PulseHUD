package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class FPSWidget extends Widget {
    private int cachedFps = 0;

    public FPSWidget() {
        super("fps", 65, 16);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showFPS;
    }

    @Override
    public void updateData() {
        cachedFps = mc.getCurrentFps();
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        drawGlassPanel(context, x, y, width, height);
        
        String text = "FPS: " + cachedFps;
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;
        int textWidth = textRenderer.getWidth(text);
        int drawX = x + (width - textWidth) / 2;
        int drawY = y + (height - 8) / 2;

        context.drawText(textRenderer, text, drawX, drawY, ThemeManager.getText(), false);
    }
}
