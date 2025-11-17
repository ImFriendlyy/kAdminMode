package vv0ta3fa9.plugin.kAdminMode.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vv0ta3fa9.plugin.kAdminMode.kAdminMode;
import vv0ta3fa9.plugin.kAdminMode.utils.Color.Colorizer;
import vv0ta3fa9.plugin.kAdminMode.utils.Color.impl.LegacyAdvancedColorizer;
import vv0ta3fa9.plugin.kAdminMode.utils.Color.impl.LegacyColorizer;
import vv0ta3fa9.plugin.kAdminMode.utils.Color.impl.VanillaColorizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final kAdminMode plugin;
    protected FileConfiguration config;
    private File configFile;
    public Colorizer COLORIZER;

    public ConfigManager(kAdminMode plugin) {
        this.plugin = plugin;
        loadConfigFiles();
    }

    private void loadConfigFiles() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", false);
            } catch (Exception e) {
                plugin.getLogger().warning("Не удалось сохранить config.yml: " + e.getMessage());
                plugin.getLogger().warning("Создайте папку plugins/kAdminMode/ вручную и дайте права на запись");
            }
        }
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }
    public boolean getBoolean(String path, boolean def) {
        if (config == null) return def;
        return config.contains(path) ? config.getBoolean(path) : def;
    }

    public String getString(String path, String def) {
        if (config == null) return def;
        return config.contains(path) ? config.getString(path) : def;
    }

    public List<String> getStringList(String path) {
        if (config == null) return new ArrayList<>();
        return config.getStringList(path);
    }

    public boolean getDebug() {
        return getBoolean("debug", true);
    }

    public void setupColorizer() {
        COLORIZER = switch (getString("serializer", "LEGACY").toUpperCase()) {
            case "LEGACY" -> new LegacyColorizer(plugin);
            case "LEGACY_ADVANCED" -> new LegacyAdvancedColorizer(plugin);
            default -> new VanillaColorizer(plugin);
        };
    }

    public List<String> getCommandList() {
        return getStringList("commands-list");
    }

    public boolean getLogSwitch() {
        return getBoolean("log", true);
    }
}

