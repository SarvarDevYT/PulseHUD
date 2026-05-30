package com.pulsehud.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pulsehud.animation.Animation;
import com.pulsehud.animation.Easing;
import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.WidgetManager;
import com.pulsehud.widget.impl.SessionStatsWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HudRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Animation trackers for smooth transitions
    private static final Animation hotbarSlotAnim = new Animation(0.0f);
    private static final Animation xpFillAnim = new Animation(0.0f);
    private static final Animation healthFillAnim = new Animation(0.0f);
    private static final Animation foodFillAnim = new Animation(0.0f);
    private static final Animation airFillAnim = new Animation(0.0f);
    private static final Animation globalOpacityAnim = new Animation(0.85f);
    private static final Animation idleSaturationAnim = new Animation(1.0f);
    
    // Low health pulse state
    private static float heartPulseTime = 0.0f;
    
    // Dynamic states
    private static long lastCombatTime = 0;
    private static long lastInputTime = System.currentTimeMillis();
    private static double lastPlayerX = 0;
    private static double lastPlayerY = 0;
    private static double lastPlayerZ = 0;
    private static float lastPlayerYaw = 0;
    private static float lastPlayerPitch = 0;

    // Track combat triggers
    public static void registerCombatAction() {
        lastCombatTime = System.currentTimeMillis();
    }

    public static void renderHUD(DrawContext context, float tickDelta) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (!config.enabled || mc.options.hudHidden || mc.player == null) {
            return;
        }

        PlayerEntity player = mc.player;
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // 1. Tick State Trackers
        tickStateTrackers(player);

        // 2. Compute Opacity & Saturation
        float opacity = computeHUDOpacity(config);
        updateIdleSaturation();
        
        // 3. Render Vignettes
        renderScreenVignettes(context, screenWidth, screenHeight, player, config);

        // 4. Render Main HUD Components
        int centerX = screenWidth / 2;
        int bottomY = screenHeight;

        // Apply global scale (1.20.1 uses MatrixStack with push/pop/scale(x,y,z))
        context.getMatrices().push();
        context.getMatrices().scale(config.hudScale, config.hudScale, 1.0f);
        
        // Scale conversion for centering
        float invScale = 1.0f / config.hudScale;
        int scaledCenterX = (int) (centerX * invScale);
        int scaledBottomY = (int) (bottomY * invScale);

        if (config.showHotbar) {
            int[] p = hudPos("hotbar", -91, -23, scaledCenterX, scaledBottomY);
            renderCustomHotbarAt(context, p[0], p[1], player, opacity, tickDelta);
            WidgetManager.reportHudBounds("hotbar", p[0], p[1], 182, 22);
        }
        if (config.showXp) {
            int[] p = hudPos("xp", -91, -28, scaledCenterX, scaledBottomY);
            renderCustomXPBarAt(context, p[0], p[1], player, opacity);
            WidgetManager.reportHudBounds("xp", p[0], p[1], 182, 4);
        }
        if (config.showHealth) {
            int[] p = hudPos("health", -122, -60, scaledCenterX, scaledBottomY);
            renderLeftPanelAt(context, p[0], p[1], player, opacity, config);
            WidgetManager.reportHudBounds("health", p[0], p[1], 110, 30);
        }
        if (config.showHunger) {
            int[] p = hudPos("hunger", 12, -60, scaledCenterX, scaledBottomY);
            renderRightPanelAt(context, p[0], p[1], player, opacity, config);
            WidgetManager.reportHudBounds("hunger", p[0], p[1], 110, 30);
        }
        if (config.showArmor) {
            int[] p = hudPos("armor", -122, -90, scaledCenterX, scaledBottomY);
            renderArmorVerticalAt(context, p[0], p[1], player, opacity);
            WidgetManager.reportHudBounds("armor", p[0], p[1], 24, 78);
        }

        context.getMatrices().pop();
    }

    private static int[] hudPos(String id, int defX, int defY, int cx, int by) {
        int[] p = PulseConfigManager.getConfig().positions.get(id);
        if (p != null && p.length >= 3 && p[2] == 4) return new int[]{ cx + p[0], by + p[1] };
        return new int[]{ cx + defX, by + defY };
    }

    private static void tickStateTrackers(PlayerEntity player) {
        long now = System.currentTimeMillis();

        // Track inputs/activity
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        if (x != lastPlayerX || y != lastPlayerY || z != lastPlayerZ || yaw != lastPlayerYaw || pitch != lastPlayerPitch) {
            lastInputTime = now;
            lastPlayerX = x;
            lastPlayerY = y;
            lastPlayerZ = z;
            lastPlayerYaw = yaw;
            lastPlayerPitch = pitch;
        }

        // Trigger combat if player took damage recently
        if (player.hurtTime > 0) {
            lastCombatTime = now;
        }

        // Update heart pulsing time
        heartPulseTime += 0.15f;
        if (heartPulseTime > (float) Math.PI * 2) {
            heartPulseTime -= (float) Math.PI * 2;
        }
    }

    private static float computeHUDOpacity(PulseConfig config) {
        long now = System.currentTimeMillis();
        float targetOpacity = config.baseOpacity;

        // Combat Mode high visibility override
        boolean inCombat = config.combatMode && (now - lastCombatTime < 8000);
        if (inCombat) {
            targetOpacity = 1.0f;
        } else if (config.idleMode && (now - lastInputTime > 15000)) {
            // Idle mode fade
            targetOpacity = config.idleOpacity;
        }

        globalOpacityAnim.transitionTo(targetOpacity, 300, Easing.EASE_OUT_QUAD);
        return globalOpacityAnim.getValue();
    }

    private static void updateIdleSaturation() {
        long now = System.currentTimeMillis();
        boolean idle = PulseConfigManager.getConfig().idleMode && (now - lastInputTime > 15000);
        float target = idle ? 0.0f : 1.0f;
        idleSaturationAnim.transitionTo(target, 600, Easing.EASE_OUT_QUAD);
        ThemeManager.setIdleSaturation(idleSaturationAnim.getValue());
    }

    private static void renderScreenVignettes(DrawContext context, int w, int h, PlayerEntity player, PulseConfig config) {
        long now = System.currentTimeMillis();
        
        // 1. Low Health Vignette (Pulsing Red)
        if (config.lowHealthVignette && player.getHealth() <= 6.0f) {
            float pulse = (float) (Math.sin(heartPulseTime * 1.5f) * 0.5f + 0.5f);
            int redColor = ((int) (pulse * 0x33) << 24) | 0xFF0000;
            // Draw a frame vignette
            drawVignetteBorder(context, w, h, redColor);
        }

        // 2. Combat Vignette (Crimson Border)
        boolean inCombat = config.combatMode && (now - lastCombatTime < 8000);
        if (inCombat && player.getHealth() > 6.0f) {
            int combatColor = 0x22FF0000; // Solid subtle crimson
            drawVignetteBorder(context, w, h, combatColor);
        }

        // 3. Underwater vignette (Elegant blue)
        if (config.waterOverlay && player.isSubmergedIn(FluidTags.WATER)) {
            int waterColor = 0x330088FF;
            drawVignetteBorder(context, w, h, waterColor);
        }
    }

    private static void drawVignetteBorder(DrawContext context, int w, int h, int color) {
        // Draw standard border margins fading slightly inwards
        context.fill(0, 0, w, 2, color); // Top
        context.fill(0, h - 2, w, h, color); // Bottom
        context.fill(0, 2, 2, h - 2, color); // Left
        context.fill(w - 2, 2, w, h - 2, color); // Right
    }

    private static void renderCustomHotbarAt(DrawContext context, int x, int y, PlayerEntity player, float opacity, float tickDelta) {
        int w = 182;
        int h = 22;

        int bg = applyOpacity(ThemeManager.getBg(), opacity);
        int border = applyOpacity(ThemeManager.getPrimary(), opacity);

        // Glassmorphism base container
        drawGlassPanel(context, x, y, w, h, bg, border);

        // Draw slots separating dots
        int dotColor = applyOpacity(0x33FFFFFF, opacity);
        for (int i = 1; i < 9; i++) {
            context.fill(x + i * 20, y + 2, x + i * 20 + 1, y + 20, dotColor);
        }

        // Animated Hotbar Active Slot Highlight selection
        int selectedSlot = player.getInventory().selectedSlot;
        float targetSlotX = x + selectedSlot * 20 + 1;
        
        hotbarSlotAnim.transitionTo(targetSlotX, 120, Easing.EASE_OUT_CUBIC);
        float animX = hotbarSlotAnim.getValue();

        // Draw Glowing Selector Rectangle
        int activeGlow = applyOpacity(ThemeManager.getSecondary(), opacity);
        context.fill((int) animX, y + 1, (int) animX + 19, y + 2, activeGlow); // Top
        context.fill((int) animX, y + 20, (int) animX + 19, y + 21, activeGlow); // Bottom
        context.fill((int) animX, y + 1, (int) animX + 1, y + 21, activeGlow); // Left
        context.fill((int) animX + 19, y + 1, (int) animX + 20, y + 21, activeGlow); // Right
        
        // Solid accent filling
        int selectionFill = applyOpacity(ThemeManager.getPrimary() & 0x00FFFFFF | 0x1A000000, opacity);
        context.fill((int) animX + 1, y + 2, (int) animX + 19, y + 20, selectionFill);

        // Render Hotbar Items
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                int itemX = x + i * 20 + 3;
                int itemY = y + 3;
                // Render Stack with count text
                context.drawItem(stack, itemX, itemY);
                context.drawItemInSlot(mc.textRenderer, stack, itemX, itemY);
            }
        }
    }

    private static void renderCustomXPBarAt(DrawContext context, int x, int y, PlayerEntity player, float opacity) {
        int w = 182;
        int h = 4;

        int bg = applyOpacity(0x4D000000, opacity);
        int border = applyOpacity(0xAA000000, opacity);

        // Glass boundary
        context.fill(x, y, x + w, y + h, bg);
        context.fill(x - 1, y - 1, x + w + 1, y, border);
        context.fill(x - 1, y + h, x + w + 1, y + h + 1, border);
        context.fill(x - 1, y, x, y + h, border);
        context.fill(x + w, y, x + w + 1, y + h, border);

        // Animated Interpolation of progress bar fill
        float progress = player.experienceProgress;
        xpFillAnim.transitionTo(progress, 250, Easing.EASE_OUT_QUAD);
        float currentProgress = xpFillAnim.getValue();

        int fillWidth = (int) (currentProgress * w);
        if (fillWidth > 0) {
            int fillPrimary = applyOpacity(ThemeManager.getPrimary(), opacity);
            int fillSecondary = applyOpacity(ThemeManager.getSecondary(), opacity);
            // Draw dual gradient fill
            context.fillGradient(x, y, x + fillWidth, y + h, fillPrimary, fillSecondary);
        }

        // Renders XP level text centered
        if (player.experienceLevel > 0) {
            String lvlStr = String.valueOf(player.experienceLevel);
            int lvlWidth = mc.textRenderer.getWidth(lvlStr);
            int textX = x + 91 - lvlWidth / 2;
            int textY = y - 9;
            
            // Text shadow manually
            context.drawText(mc.textRenderer, lvlStr, textX - 1, textY, applyOpacity(0xFF000000, opacity), false);
            context.drawText(mc.textRenderer, lvlStr, textX + 1, textY, applyOpacity(0xFF000000, opacity), false);
            context.drawText(mc.textRenderer, lvlStr, textX, textY - 1, applyOpacity(0xFF000000, opacity), false);
            context.drawText(mc.textRenderer, lvlStr, textX, textY + 1, applyOpacity(0xFF000000, opacity), false);
            
            context.drawText(mc.textRenderer, lvlStr, textX, textY, applyOpacity(ThemeManager.getPrimary(), opacity), false);
        }
    }

    private static void renderLeftPanelAt(DrawContext context, int x, int y, PlayerEntity player, float opacity, PulseConfig config) {
        int w = 110;
        int h = 30;

        // Apply low health shake
        boolean isLowHealth = player.getHealth() <= 6.0f;
        int shakeX = 0;
        int shakeY = 0;
        if (isLowHealth) {
            shakeX = (int) (Math.sin(heartPulseTime * 2.5f) * 2.0f);
            shakeY = (int) (Math.cos(heartPulseTime * 2.5f) * 1.5f);
        }

        int finalX = x + shakeX;
        int finalY = y + shakeY;

        int bg = applyOpacity(ThemeManager.getBg(), opacity);
        int border = applyOpacity(isLowHealth ? 0xFFFF0055 : ThemeManager.getPrimary(), opacity);

        // Glass panel container
        drawGlassPanel(context, finalX, finalY, w, h, bg, border);

        TextRenderer textRenderer = mc.textRenderer;

        // Animated health heart meters
        if (config.showHealth) {
            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            float absorption = player.getAbsorptionAmount();

            healthFillAnim.transitionTo(health / maxHealth, 150, Easing.EASE_OUT_QUAD);
            float pct = healthFillAnim.getValue();

            int hpX = finalX + 6;
            int hpY = finalY + 16;
            
            // Health bar visual backdrop
            context.fill(hpX, hpY, hpX + 98, hpY + 6, applyOpacity(0x33000000, opacity));

            int barWidth = (int) (pct * 98);
            if (barWidth > 0) {
                int hpColor = applyOpacity(0xFFFF2222, opacity); // Vanilla Red
                if (player.hasStatusEffect(StatusEffects.POISON)) {
                    hpColor = applyOpacity(0xFF7E8C24, opacity); // Poison Green
                } else if (player.hasStatusEffect(StatusEffects.WITHER)) {
                    hpColor = applyOpacity(0xFF262626, opacity); // Wither Black
                }
                
                context.fill(hpX, hpY, hpX + barWidth, hpY + 6, hpColor);
            }

            // Draw Absorption layer if any
            if (absorption > 0) {
                float absPct = Math.min(1.0f, absorption / maxHealth);
                int absWidth = (int) (absPct * 98);
                context.fill(hpX, hpY + 4, hpX + absWidth, hpY + 6, applyOpacity(0xFFFFD700, opacity)); // Yellow Gold
            }

            // Display floating text indicator in small font
            String hpText = String.format("%.0f/%.0f", health + absorption, maxHealth);
            int textW = textRenderer.getWidth(hpText);
            context.drawText(textRenderer, hpText, hpX + 49 - textW / 2, hpY - 1, applyOpacity(0xFFFFFFFF, opacity), false);
        }
    }

    private static void renderArmorVerticalAt(DrawContext context, int x, int y, PlayerEntity player, float opacity) {
        int slotSize = 18;
        int w = 24;
        int h = slotSize * 4 + 6;

        int bg = applyOpacity(ThemeManager.getBg(), opacity);
        int border = applyOpacity(ThemeManager.getPrimary(), opacity);
        drawGlassPanel(context, x, y, w, h, bg, border);

        for (int i = 3; i >= 0; i--) {
            int slotY = y + 3 + (3 - i) * slotSize;
            int slotX = x + 4;
            int slotColor = applyOpacity(0x33FFFFFF, opacity);
            context.fill(slotX, slotY, slotX + 16, slotY + 16, slotColor);

            if (player.getInventory().armor.get(i).isEmpty()) continue;
            ItemStack armorStack = player.getInventory().armor.get(i);
            context.drawItem(armorStack, slotX, slotY);
            context.drawItemInSlot(mc.textRenderer, armorStack, slotX, slotY);

            int maxDmg = armorStack.getMaxDamage();
            if (maxDmg > 0) {
                int dmg = armorStack.getDamage();
                int remain = maxDmg - dmg;
                String durStr = String.valueOf(remain);
                context.drawText(mc.textRenderer, durStr,
                    x + w - mc.textRenderer.getWidth(durStr) - 3, slotY + 10,
                    remain < maxDmg / 4 ? 0xFFFF4444 : 0xFFAAAAAA, true);
            }
        }
    }

    private static void renderRightPanelAt(DrawContext context, int x, int y, PlayerEntity player, float opacity, PulseConfig config) {
        int w = 110;
        int h = 30;

        int bg = applyOpacity(ThemeManager.getBg(), opacity);
        int border = applyOpacity(ThemeManager.getPrimary(), opacity);

        // Draw glass block container
        drawGlassPanel(context, x, y, w, h, bg, border);

        // Line 1: Sprint Energy / Chevrons
        int energyX = x + 6;
        int energyY = y + 4;
        
        if (player.isSprinting() && config.speedPulse) {
            // Speed Pulse Chevrons
            int count = (int) (System.currentTimeMillis() / 150 % 6);
            int color = applyOpacity(ThemeManager.getSecondary(), opacity);
            for (int i = 0; i < 5; i++) {
                int drawColor = (i == count) ? color : applyOpacity(0x33FFFFFF, opacity);
                context.fill(energyX + i * 8, energyY + 3, energyX + i * 8 + 4, energyY + 5, drawColor);
            }
        } else {
            // Idle energy
            context.fill(energyX, energyY + 3, energyX + 38, energyY + 5, applyOpacity(0x44FFFFFF, opacity));
        }

        // Line 2: Hunger and Oxygen
        int foodLevel = player.getHungerManager().getFoodLevel();
        foodFillAnim.transitionTo(foodLevel / 20.0f, 200, Easing.EASE_OUT_QUAD);
        float foodPct = foodFillAnim.getValue();

        int foodX = x + 6;
        int foodY = y + 16;

        // Hunger background
        context.fill(foodX, foodY, foodX + 98, foodY + 6, applyOpacity(0x33000000, opacity));

        int foodWidth = (int) (foodPct * 98);
        if (foodWidth > 0) {
            int foodColor = applyOpacity(0xFFFF8A00, opacity); // Golden drumstick orange
            if (player.hasStatusEffect(StatusEffects.HUNGER)) {
                foodColor = applyOpacity(0xFF5A8442, opacity); // Rotting green
            }
            context.fill(foodX, foodY, foodX + foodWidth, foodY + 6, foodColor);
        }

        // Render Oxygen bubbles if submerged
        int air = player.getAir();
        int maxAir = player.getMaxAir();
        if (air < maxAir) {
            airFillAnim.transitionTo((float) air / maxAir, 150, Easing.EASE_OUT_QUAD);
            float airPct = airFillAnim.getValue();
            
            int airX = x + 6;
            int airY = y + 8;
            int airWidth = (int) (airPct * 98);
            
            // Bubble oxygen progress bar directly on top of hunger
            context.fill(airX, airY, airX + 98, airY + 2, applyOpacity(0x33000000, opacity));
            if (airWidth > 0) {
                context.fill(airX, airY, airX + airWidth, airY + 2, applyOpacity(0xFF00AAFF, opacity));
            }
        }
    }

    private static void drawGlassPanel(DrawContext context, int x, int y, int w, int h, int bgColor, int borderColor) {
        // Fill main rounded frame
        context.fill(x + 1, y + 1, x + w - 1, y + h - 1, bgColor);

        // Thin glow frame borders
        context.fill(x + 1, y, x + w - 1, y + 1, borderColor); // Top
        context.fill(x + 1, y + h - 1, x + w - 1, y + h, borderColor); // Bottom
        context.fill(x, y + 1, x + 1, y + h - 1, borderColor); // Left
        context.fill(x + w - 1, y + 1, x + w, y + h - 1, borderColor); // Right
    }

    private static int applyOpacity(int color, float opacity) {
        int a = (int) (((color >> 24) & 0xFF) * opacity);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
