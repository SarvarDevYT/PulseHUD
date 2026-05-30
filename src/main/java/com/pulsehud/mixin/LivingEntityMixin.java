package com.pulsehud.mixin;

import com.pulsehud.widget.impl.SessionStatsWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void onHandleStatus(byte status, CallbackInfo ci) {
        if (status == 3) {
            LivingEntity entity = (LivingEntity) (Object) this;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                // If player was the last attacker of this dying entity, increment session kills
                if (entity.getAttacker() == mc.player) {
                    SessionStatsWidget.incrementKills();
                }
            }
        }
    }
}
