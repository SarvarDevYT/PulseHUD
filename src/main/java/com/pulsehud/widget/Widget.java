package com.pulsehud.widget;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.theme.ThemeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public abstract class Widget {
    protected final String id;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected final int defaultWidth;
    protected final int defaultHeight;
    protected int anchor;
    protected final MinecraftClient mc;
    protected TextRenderer textRenderer;

    public Widget(String id, int defaultWidth, int defaultHeight) {
        this.id = id;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.width = defaultWidth;
        this.height = defaultHeight;
        this.mc = MinecraftClient.getInstance();
        this.textRenderer = null;
    }

    protected TextRenderer getTextRenderer() {
        if (this.textRenderer == null) {
            this.textRenderer = mc.textRenderer;
        }
        return this.textRenderer;
    }

    public String getId() {
        return id;
    }

    public abstract boolean isEnabled(PulseConfig config);

    public abstract void updateData();

    public abstract void render(DrawContext context, float tickDelta, int mouseX, int mouseY);

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width
            && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public void updatePosition(int newX, int newY, int screenWidth, int screenHeight) {
        PulseConfig config = PulseConfigManager.getConfig();
        switch (this.anchor) {
            case 0: config.positions.put(id, new int[]{ newX, newY, 0, width, height }); break;
            case 1: config.positions.put(id, new int[]{ newX - (screenWidth - this.width), newY, 1, width, height }); break;
            case 2: config.positions.put(id, new int[]{ newX, newY - (screenHeight - this.height), 2, width, height }); break;
            case 3: config.positions.put(id, new int[]{ newX - (screenWidth - this.width), newY - (screenHeight - this.height), 3, width, height }); break;
            case 4: config.positions.put(id, new int[]{ newX - screenWidth / 2, newY - screenHeight, 4, width, height }); break;
            case 5: config.positions.put(id, new int[]{ newX - (screenWidth / 2 - this.width / 2), newY, 5, width, height }); break;
        }
        this.x = newX;
        this.y = newY;
    }

    // Utility: Draw a premium, glowy glassmorphic panel
    protected void drawGlassPanel(DrawContext context, int x, int y, int width, int height) {
        PulseConfig config = PulseConfigManager.getConfig();
        float opacity = config.widgetOpacity;
        int bgColor = applyAlpha(ThemeManager.getBg(), opacity);
        int borderColor = ThemeManager.getPrimary();
        int glowColor = ThemeManager.getGlow();

        // 1. Draw solid dark panel background
        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, bgColor);

        // 2. Draw outer soft glow effect (multiple layers of thin translucent rects)
        drawSoftGlow(context, x, y, width, height, glowColor);

        // 3. Draw sharp glass border
        context.fill(x + 1, y, x + width - 1, y + 1, borderColor); // Top
        context.fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor); // Bottom
        context.fill(x, y + 1, x + 1, y + height - 1, borderColor); // Left
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, borderColor); // Right
    }

    private static int applyAlpha(int color, float opacity) {
        int a = (int) (((color >> 24) & 0xFF) * opacity);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void drawSoftGlow(DrawContext context, int x, int y, int width, int height, int glowColor) {
        // Simple 2-layer glow outline
        int alphaGlow1 = (glowColor & 0x00FFFFFF) | 0x22000000;
        int alphaGlow2 = (glowColor & 0x00FFFFFF) | 0x11000000;

        // Inner glowing border
        context.fill(x - 1, y, x, y + height, alphaGlow1);
        context.fill(x + width, y, x + width + 1, y + height, alphaGlow1);
        context.fill(x, y - 1, x + width, y, alphaGlow1);
        context.fill(x, y + height, x + width, y + height + 1, alphaGlow1);

        // Outer soft glow
        context.fill(x - 2, y - 1, x - 1, y + height + 1, alphaGlow2);
        context.fill(x + width + 1, y - 1, x + width + 2, y + height + 1, alphaGlow2);
        context.fill(x - 1, y - 2, x + width + 1, y - 1, alphaGlow2);
        context.fill(x - 1, y + height + 1, x + width + 1, y + height + 2, alphaGlow2);
    }

    public void resolvePosition(int screenWidth, int screenHeight) {
        PulseConfig config = PulseConfigManager.getConfig();
        int[] posInfo = config.positions.get(id);
        if (posInfo == null) {
            return;
        }

        int offsetX = posInfo[0];
        int offsetY = posInfo[1];
        this.anchor = posInfo[2];

        // Read custom size if present, else keep default
        if (posInfo.length >= 5) {
            this.width = Math.max(posInfo[3], 20);
            this.height = Math.max(posInfo[4], 16);
        } else {
            this.width = defaultWidth;
            this.height = defaultHeight;
        }

        switch (this.anchor) {
            case 0:
                this.x = offsetX;
                this.y = offsetY;
                break;
            case 1:
                this.x = screenWidth - this.width + offsetX;
                this.y = offsetY;
                break;
            case 2:
                this.x = offsetX;
                this.y = screenHeight - this.height + offsetY;
                break;
            case 3:
                this.x = screenWidth - this.width + offsetX;
                this.y = screenHeight - this.height + offsetY;
                break;
            case 4:
                this.x = screenWidth / 2 + offsetX;
                this.y = screenHeight + offsetY;
                break;
            case 5:
                this.x = screenWidth / 2 - this.width / 2 + offsetX;
                this.y = offsetY;
                break;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setSize(int w, int h) {
        this.width = Math.max(w, 20);
        this.height = Math.max(h, 16);
        PulseConfig config = PulseConfigManager.getConfig();
        int[] pos = config.positions.get(id);
        if (pos != null) {
            config.positions.put(id, new int[]{ pos[0], pos[1], pos[2], this.width, this.height });
        }
    }
}
