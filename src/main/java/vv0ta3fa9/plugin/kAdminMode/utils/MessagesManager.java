package vv0ta3fa9.plugin.kAdminMode.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vv0ta3fa9.plugin.kAdminMode.kAdminMode;

import java.io.File;

public class MessagesManager {
    private final kAdminMode plugin;
    private FileConfiguration messagesconfig;
    private File messagesConfigFile;

    public MessagesManager(kAdminMode plugin) {

        this.plugin = plugin;
        loadFiles();
    }

    private void loadFiles() {
        messagesConfigFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        reloadMessages();
    }
    public void reloadMessages() {
        if (messagesConfigFile == null) {
            messagesConfigFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (messagesConfigFile.exists()) {
            messagesconfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        } else {
            plugin.getLogger().warning("Messages file not found: " + messagesConfigFile.getPath());
        }
    }

    private String getMessage(String path, String defaultValue) {
        if (messagesconfig == null) return defaultValue;
        return messagesconfig.getString(path, defaultValue);
    }

    // ---- system ---- //
    public String nopermission() {
        return getMessage("system.no-permission", "§cУ тебя нет прав.");
    }
    public String reloadplugin() {
        return getMessage("system.reload-plugin", "§aБип-пуп успешная перезагрузкая");
    }
    public String playeronly() {
        return messagesconfig.getString("system.console-only", "§cЭта команда доступна только игрокам!");
    }
    public String enableMode() {
        return getMessage("commands.switch.enable","&6kAdminMode &7>> &eВы &aвошли &eв режим работы!");
    }
    public String disableMode() {
        return getMessage("commands.switch.disable","&6kAdminMode &7>> &eВы &cвышли &eиз режима работы!");
    }
    public String notinmode() {
        return getMessage("another.not-in-mode", "&6kAdminMode &7>> &cВы не находитесь в режиме работы!");
    }
    public String joined_admin(String nickname) {
        return messagesconfig.getString("another.joined-admin", "&fАдминистратор &3%player% &fзашел в режим работы.")
                .replace("%player%", nickname);
    }
}
