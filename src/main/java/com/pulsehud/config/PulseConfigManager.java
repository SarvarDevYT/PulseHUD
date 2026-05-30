package com.pulsehud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PulseConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;
    private static PulseConfig config;

    public static void init() {
        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pulsehud.json");
        load();
    }

    public static PulseConfig getConfig() {
        if (config == null) {
            load();
        }
        return config;
    }

    public static void load() {
        if (configFile != null && configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, PulseConfig.class);
                if (config == null) {
                    config = new PulseConfig();
                }
            } catch (Exception e) {
                System.err.println("[PulseHUD] Failed to load config, generating defaults: " + e.getMessage());
                config = new PulseConfig();
                save();
            }
        } else {
            config = new PulseConfig();
            save();
        }
    }

    public static void save() {
        if (configFile == null) {
            configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pulsehud.json");
        }
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            System.err.println("[PulseHUD] Failed to save config: " + e.getMessage());
        }
    }
}
