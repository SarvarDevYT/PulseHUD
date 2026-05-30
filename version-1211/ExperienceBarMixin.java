package com.pulsehud.mixin;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBar.class)
public class ExperienceBarMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderExperienceBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (config.enabled && config.showXp) {
            ci.cancel();
        }
    }
}