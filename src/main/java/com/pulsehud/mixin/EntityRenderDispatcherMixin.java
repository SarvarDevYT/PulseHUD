package com.pulsehud.mixin;

import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.theme.ThemeManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow private boolean renderHitboxes;

    @Inject(method = "render", at = @At("TAIL"))
    private <E extends Entity> void onRenderTail(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!PulseConfigManager.getConfig().enabled) return;
        if (!this.renderHitboxes) return;
        if (entity.isInvisible()) return;

        int color = ThemeManager.getPrimary();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        Box box = entity.getBoundingBox().offset(-x, -y, -z);
        WorldRenderer.drawBox(matrices, vertexConsumer, (float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ, r, g, b, 1.0f);
    }
}
