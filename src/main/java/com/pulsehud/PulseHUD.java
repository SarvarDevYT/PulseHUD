package com.pulsehud;

import com.pulsehud.config.PulseConfigManager;
import net.fabricmc.api.ModInitializer;

public class PulseHUD implements ModInitializer {
    public static final String MOD_ID = "pulsehud";

    @Override
    public void onInitialize() {
        System.out.println("[PulseHUD] Initializing Core mod systems...");
        PulseConfigManager.init();
    }

    public static void registerWidget(com.pulsehud.widget.Widget widget) {
        com.pulsehud.widget.WidgetManager.registerWidget(widget);
    }
}
