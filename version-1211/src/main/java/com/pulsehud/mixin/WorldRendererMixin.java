package com.pulsehud.mixin;

import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.theme.ThemeManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onDrawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!PulseConfigManager.getConfig().enabled) return;

        ci.cancel();

        int color = ThemeManager.getPrimary();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        VoxelShape shape = state.getOutlineShape(entity.getEntityWorld(), pos, ShapeContext.of(entity));
        WorldRenderer.drawShapeOutline(matrices, vertexConsumer, shape,
            pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ,
            r, g, b, 0.4f);
    }
}
