package com.pulsehud;

import com.pulsehud.config.ModMenuIntegration;
import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.WidgetManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PulseHUDClient implements ClientModInitializer {

    private static KeyBinding editModeKey;
    private static KeyBinding configKey;

    @Override
    public void onInitializeClient() {
        System.out.println("[PulseHUD] Initializing Client mod systems...");

        editModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.pulsehud.editmode",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KeyBinding.Category.MISC
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.pulsehud.config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            KeyBinding.Category.MISC
        ));

        WidgetManager.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                ThemeManager.tick();

                PulseConfig config = PulseConfigManager.getConfig();

                while (editModeKey.wasPressed()) {
                    config.editMode = !config.editMode;
                    PulseConfigManager.save();
                }

                if (config.editMode && client.mouse.isCursorLocked()) {
                    client.mouse.unlockCursor();
                }

                while (configKey.wasPressed()) {
                    client.setScreen(ModMenuIntegration.buildConfigScreen(client.currentScreen));
                }
            }
        });
    }
}
