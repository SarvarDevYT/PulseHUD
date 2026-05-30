package com.pulsehud.mixin;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.hud.HitMarkerManager;
import com.pulsehud.hud.HudRenderer;
import com.pulsehud.widget.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
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
    private void onRenderTail(DrawContext context, float tickDelta, CallbackInfo ci) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (config.enabled) {
            MinecraftClient mc = MinecraftClient.getInstance();
            double scaleX = (double) context.getScaledWindowWidth() / mc.getWindow().getWidth();
            double scaleY = (double) context.getScaledWindowHeight() / mc.getWindow().getHeight();
            int mouseX = (int) (mc.mouse.getX() * scaleX);
            int mouseY = (int) (mc.mouse.getY() * scaleY);

            HudRenderer.renderHUD(context, tickDelta);
            WidgetManager.renderAll(context, tickDelta, mouseX, mouseY);
            HitMarkerManager.render(context);
        }
    }
}
