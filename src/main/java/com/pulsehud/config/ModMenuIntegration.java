package com.pulsehud.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::buildConfigScreen;
    }

    public static Screen buildConfigScreen(Screen parent) {
        PulseConfig config = PulseConfigManager.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.pulsehud.title"))
                .setSavingRunnable(() -> {
                    if (config.resetPositionsOnSave) {
                        config.resetDefaultPositions();
                        config.resetPositionsOnSave = false;
                    }
                    PulseConfigManager.save();
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // CATEGORY 1: GENERAL
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.pulsehud.category.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.enabled"), config.enabled)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.enabled.tooltip"))
                .setSaveConsumer(val -> config.enabled = val)
                .build());

        general.addEntry(entryBuilder.startStringDropdownMenu(Text.translatable("config.pulsehud.option.theme"), config.theme)
                .setDefaultValue("neon")
                .setSelections(List.of("neon", "vanilla", "cyberpunk", "minimal", "rgb"))
                .setTooltip(Text.translatable("config.pulsehud.option.theme.tooltip"))
                .setSaveConsumer(val -> config.theme = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("config.pulsehud.option.hud_scale"), config.hudScale)
                .setDefaultValue(1.0f)
                .setMin(0.5f)
                .setMax(2.0f)
                .setTooltip(Text.translatable("config.pulsehud.option.hud_scale.tooltip"))
                .setSaveConsumer(val -> config.hudScale = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("config.pulsehud.option.base_opacity"), config.baseOpacity)
                .setDefaultValue(0.85f)
                .setMin(0.1f)
                .setMax(1.0f)
                .setTooltip(Text.translatable("config.pulsehud.option.base_opacity.tooltip"))
                .setSaveConsumer(val -> config.baseOpacity = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("config.pulsehud.option.idle_opacity"), config.idleOpacity)
                .setDefaultValue(0.35f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setTooltip(Text.translatable("config.pulsehud.option.idle_opacity.tooltip"))
                .setSaveConsumer(val -> config.idleOpacity = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("config.pulsehud.option.animation_speed"), config.animationSpeed)
                .setDefaultValue(1.0f)
                .setMin(0.1f)
                .setMax(3.0f)
                .setTooltip(Text.translatable("config.pulsehud.option.animation_speed.tooltip"))
                .setSaveConsumer(val -> config.animationSpeed = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("config.pulsehud.option.glow_intensity"), config.glowIntensity)
                .setDefaultValue(1.0f)
                .setMin(0.0f)
                .setMax(2.0f)
                .setTooltip(Text.translatable("config.pulsehud.option.glow_intensity.tooltip"))
                .setSaveConsumer(val -> config.glowIntensity = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.biome_reactivity"), config.biomeReactivity)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.biome_reactivity.tooltip"))
                .setSaveConsumer(val -> config.biomeReactivity = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.reset_positions"), config.resetPositionsOnSave)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.pulsehud.option.reset_positions.tooltip"))
                .setSaveConsumer(val -> config.resetPositionsOnSave = val)
                .build());

        // CATEGORY 2: ELEMENTS
        ConfigCategory elements = builder.getOrCreateCategory(Text.translatable("config.pulsehud.category.elements"));

        elements.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_health"), config.showHealth)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHealth = val)
                .build());

        elements.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_hunger"), config.showHunger)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHunger = val)
                .build());

        elements.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_armor"), config.showArmor)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showArmor = val)
                .build());

        elements.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_xp"), config.showXp)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showXp = val)
                .build());

        elements.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_hotbar"), config.showHotbar)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHotbar = val)
                .build());

        // CATEGORY 3: DYNAMIC STATES
        ConfigCategory states = builder.getOrCreateCategory(Text.translatable("config.pulsehud.category.states"));

        states.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.combat_mode"), config.combatMode)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.combat_mode.tooltip"))
                .setSaveConsumer(val -> config.combatMode = val)
                .build());

        states.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.idle_mode"), config.idleMode)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.idle_mode.tooltip"))
                .setSaveConsumer(val -> config.idleMode = val)
                .build());

        states.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.low_health_vignette"), config.lowHealthVignette)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.low_health_vignette.tooltip"))
                .setSaveConsumer(val -> config.lowHealthVignette = val)
                .build());

        states.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.speed_pulse"), config.speedPulse)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.speed_pulse.tooltip"))
                .setSaveConsumer(val -> config.speedPulse = val)
                .build());

        states.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.water_overlay"), config.waterOverlay)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.water_overlay.tooltip"))
                .setSaveConsumer(val -> config.waterOverlay = val)
                .build());

        // CATEGORY 4: WIDGETS
        ConfigCategory widgets = builder.getOrCreateCategory(Text.translatable("config.pulsehud.category.widgets"));

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_fps"), config.showFPS)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showFPS = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_ping"), config.showPing)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showPing = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_coords"), config.showCoords)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showCoords = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_compass"), config.showCompass)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showCompass = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_clock"), config.showClock)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showClock = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_stats"), config.showStats)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showStats = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_rtc"), config.showRTC)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.pulsehud.option.show_rtc.tooltip"))
                .setSaveConsumer(val -> config.showRTC = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_selected_item"), config.showSelectedItem)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showSelectedItem = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_potion_effects"), config.showPotionEffects)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showPotionEffects = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_biome"), config.showBiome)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showBiome = val)
                .build());

        widgets.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.pulsehud.option.show_hit_marker"), config.showHitMarker)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHitMarker = val)
                .build());

        // CATEGORY 5: APPEARANCE
        ConfigCategory appearance = builder.getOrCreateCategory(Text.translatable("config.pulsehud.category.appearance"));

        appearance.addEntry(entryBuilder.startFloatField(Text.translatable("config.pulsehud.option.widget_opacity"), config.widgetOpacity)
                .setDefaultValue(0.5f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setTooltip(Text.translatable("config.pulsehud.option.widget_opacity.tooltip"))
                .setSaveConsumer(val -> config.widgetOpacity = val)
                .build());

        return builder.build();
    }
}
