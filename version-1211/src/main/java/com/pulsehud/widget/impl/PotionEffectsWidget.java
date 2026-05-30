package com.pulsehud.widget.impl;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;

import java.util.Collection;

public class PotionEffectsWidget extends Widget {
    private Collection<StatusEffectInstance> cachedEffects;
    private int effectCount;

    public PotionEffectsWidget() {
        super("potioneffects", 130, 80);
        this.cachedEffects = java.util.Collections.emptyList();
    }

    @Override
    public boolean isEnabled(PulseConfig config) {
        return config.showPotionEffects;
    }

    @Override
    public void updateData() {
        if (mc.player != null) {
            cachedEffects = mc.player.getStatusEffects();
            effectCount = cachedEffects.size();
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        TextRenderer textRenderer = getTextRenderer();
        if (textRenderer == null) return;

        drawGlassPanel(context, x, y, width, height);

        if (cachedEffects.isEmpty()) {
            String text = "No effects";
            int tw = textRenderer.getWidth(text);
            context.drawText(textRenderer, text, x + (width - tw) / 2, y + (height - 8) / 2, ThemeManager.getText(), false);
            return;
        }

        int lineY = y + 4;
        for (StatusEffectInstance effect : cachedEffects) {
            String name = effect.getEffectType().value().getName().getString();
            int amplifier = effect.getAmplifier() + 1;
            String dur = StatusEffectUtil.getDurationText(effect, 1.0f, 1.0f).getString();

            String line = name + (amplifier > 1 ? " " + toRoman(amplifier) : "") + " " + dur;
            int color = effect.getEffectType().value().getColor();

            context.drawText(textRenderer, line, x + 6, lineY, color, false);
            lineY += textRenderer.fontHeight + 2;
        }
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(n);
        };
    }
}
