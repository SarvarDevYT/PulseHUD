package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class SelectedItemWidget extends Widget {
    private String itemName = "";
    private ItemStack cachedStack = ItemStack.EMPTY;

    public SelectedItemWidget() {
        super("selecteditem", 110, 28);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showSelectedItem;
    }

    @Override
    public void updateData() {
        if (mc.player != null) {
            cachedStack = mc.player.getMainHandStack();
            if (!cachedStack.isEmpty()) {
                itemName = cachedStack.getName().getString();
            } else {
                itemName = "";
            }
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;

        drawGlassPanel(context, x, y, width, height);

        if (!cachedStack.isEmpty()) {
            context.drawItem(cachedStack, x + 4, y + 6);

            int txtX = x + 24;
            int txtY = y + (height - 8) / 2;
            int maxW = width - 28;
            String display = itemName;
            if (textRenderer.getWidth(display) > maxW) {
                while (textRenderer.getWidth(display + "...") > maxW && display.length() > 1) {
                    display = display.substring(0, display.length() - 1);
                }
                display += "...";
            }
            context.drawText(textRenderer, display, txtX, txtY, ThemeManager.getText(), false);
        } else {
            String text = "Empty";
            int tw = textRenderer.getWidth(text);
            context.drawText(textRenderer, text, x + (width - tw) / 2, y + (height - 8) / 2, ThemeManager.getText(), false);
        }
    }
}
