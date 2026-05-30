package com.pulsehud.mixin;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.hud.HitMarkerManager;
import com.pulsehud.hud.HudRenderer;
import com.pulsehud.widget.WidgetManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (config.enabled && config.showHotbar) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusBars(DrawContext context, CallbackInfo ci) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (config.enabled && (config.showHealth || config.showHunger || config.showArmor)) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (config.enabled) {
            float tickDelta = tickCounter.getDynamicDeltaTicks();
            HudRenderer.renderHUD(context, tickDelta);
            WidgetManager.renderAll(context, tickDelta, -1, -1);
            HitMarkerManager.render(context);
        }
    }
}