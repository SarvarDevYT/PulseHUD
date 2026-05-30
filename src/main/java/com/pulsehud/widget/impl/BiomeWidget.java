package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BiomeWidget extends Widget {
    private String biomeName = "";

    public BiomeWidget() {
        super("biome", 130, 16);
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showBiome;
    }

    @Override
    public void updateData() {
        if (mc.player != null && mc.world != null) {
            java.util.Optional<RegistryKey<Biome>> key = mc.world.getBiome(mc.player.getBlockPos()).getKey();
            if (key.isPresent()) {
                String path = key.get().getValue().getPath();
                biomeName = path.replace('_', ' ');
                String[] parts = biomeName.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].length() > 0) {
                        parts[i] = Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1);
                    }
                }
                biomeName = String.join(" ", parts);
            } else {
                biomeName = "Unknown";
            }
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;

        drawGlassPanel(context, x, y, width, height);

        int tw = textRenderer.getWidth(biomeName);
        int drawX = x + (width - tw) / 2;
        int drawY = y + (height - 8) / 2;

        context.drawText(textRenderer, biomeName, drawX, drawY, ThemeManager.getText(), false);
    }
}
