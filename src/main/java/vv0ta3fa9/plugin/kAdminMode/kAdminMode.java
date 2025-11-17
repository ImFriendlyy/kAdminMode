package vv0ta3fa9.plugin.kAdminMode;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import vv0ta3fa9.plugin.kAdminMode.main.CommandManager;
import vv0ta3fa9.plugin.kAdminMode.utils.ConfigManager;
import vv0ta3fa9.plugin.kAdminMode.utils.LogManager;
import vv0ta3fa9.plugin.kAdminMode.utils.MessagesManager;
import vv0ta3fa9.plugin.kAdminMode.utils.Utils;

import java.util.ArrayList;

public final class kAdminMode extends JavaPlugin {

    public final Server server = getServer();
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private Utils utils;
    private CommandManager commandsManager;
    private LogManager logManager;
    private Listener listener;
    private final ArrayList<String> activeAdmins = new ArrayList<>();
    private final ArrayList<String> notActiveAdmins = new ArrayList<>();


    @Override
    public void onEnable() {
        try {
            loadingConfiguration();
            configManager.setupColorizer();
            registerCommands();
            registerListeners(server.getPluginManager());
        } catch (Exception e) {
            getLogger().severe("ОШИБКА ВКЛЮЧЕНИЯ ПЛАГИНА! Выключение плагина...");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
    }

    private void loadingConfiguration() {
        try {
            configManager = new ConfigManager(this);
            utils = new Utils();
            messagesManager = new MessagesManager(this);
            commandsManager = new CommandManager(this);
            if (configManager.getLogSwitch()) {
                logManager = new LogManager(this);
            }
        } catch (Exception e) {
            getLogger().severe("ОШИБКА ЗАГРУЗКИ ПЛАГИНА! Выключение плагина...");
            e.printStackTrace();
            throw e;
        }
    }

    private void registerCommands() {
        if (getCommand("kadminmode") != null) {
            getCommand("kadminmode").setExecutor(commandsManager);
        } else {
            getLogger().severe("Команда 'kadminmode' не найдена в plugin.yml!");
        }
    }

    private void registerListeners(PluginManager pluginManager) {
        listener = new vv0ta3fa9.plugin.kAdminMode.main.Listener(this);
        pluginManager.registerEvents(listener, this);
        getLogger().info("[kAdminMode] Обработчики событий зарегистрированы успешно!");
        getLogger().info("[kAdminMode] Debug режим: " + (configManager != null ? configManager.getDebug() : "ConfigManager не инициализирован"));
    }

    // ---- Геттеры ----

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Utils getUtils() {
        return utils;
    }

    public LogManager getDataManager() { return logManager; }

    public ArrayList<String> getActiveAdmins() {
        return activeAdmins;
    }

    public ArrayList<String> getNotActiveAdmins() {
        return notActiveAdmins;
    }

    public LogManager getLogManager() {
        return logManager;
    }
}
