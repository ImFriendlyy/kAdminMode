package vv0ta3fa9.plugin.kAdminMode.main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import vv0ta3fa9.plugin.kAdminMode.kAdminMode;

import java.time.LocalDateTime;
import java.util.List;

public class Listener implements org.bukkit.event.Listener {

    private final kAdminMode plugin;

    public Listener(kAdminMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        boolean debug = plugin.getConfigManager().getDebug();

        if (debug) {
            plugin.getLogger().info("[DEBUG] ========== Игрок вышел с сервера ==========");
            plugin.getLogger().info("[DEBUG] Игрок: " + playerName);
        }

        if (plugin.getActiveAdmins().contains(playerName)) {
            plugin.getActiveAdmins().remove(playerName);

            if (debug) {
                plugin.getLogger().info("[DEBUG] Игрок был в режиме админа");
                plugin.getLogger().info("[DEBUG] Удален из списка ActiveAdmins");
            }

            if (plugin.getLogManager() != null) {
                plugin.getLogManager().writeLog(LocalDateTime.now().toString(), playerName, "quit");
                if (debug) {
                    plugin.getLogger().info("[DEBUG] Запись добавлена в лог");
                }
            }
        } else {
            if (debug) {
                plugin.getLogger().info("[DEBUG] Игрок НЕ был в режиме админа");
            }
        }

        if (plugin.getNotActiveAdmins().contains(playerName)) {
            plugin.getNotActiveAdmins().remove(playerName);
            if (debug) {
                plugin.getLogger().info("[DEBUG] Удален из списка NotActiveAdmins");
            }
        }

        if (debug) {
            plugin.getLogger().info("[DEBUG] ===========================================");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean debug = plugin.getConfigManager().getDebug();

        if (debug) {
            plugin.getLogger().info("[DEBUG] ========== Игрок зашел на сервер ==========");
            plugin.getLogger().info("[DEBUG] Игрок: " + player.getName());
            plugin.getLogger().info("[DEBUG] UUID: " + player.getUniqueId());
        }

        if (player.hasPermission("kadminmode.admin")) {
            plugin.getNotActiveAdmins().add(player.getName());

            if (debug) {
                plugin.getLogger().info("[DEBUG] Игрок имеет право kadminmode.admin");
                plugin.getLogger().info("[DEBUG] Добавлен в список NotActiveAdmins");
                plugin.getLogger().info("[DEBUG] Текущий список NotActiveAdmins: " + plugin.getNotActiveAdmins());
            }
        } else {
            if (debug) {
                plugin.getLogger().info("[DEBUG] Игрок НЕ имеет право kadminmode.admin");
                plugin.getLogger().info("[DEBUG] Не добавлен в список NotActiveAdmins");
            }
        }

        if (debug) {
            plugin.getLogger().info("[DEBUG] ===========================================");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        // Дебпг в самом начале, чтобы проверить, вызывается ли метод вообще
        plugin.getLogger().info("[DEBUG] ========== МЕТОД onCommand ВЫЗВАН ==========");
        plugin.getLogger().info("[DEBUG] Команда получена: " + event.getMessage());
        plugin.getLogger().info("[DEBUG] Игрок: " + event.getPlayer().getName());
        
        if (event.isCancelled()) {
            plugin.getLogger().warning("[DEBUG] Событие уже отменено другим плагином! Это не должно происходить при HIGHEST приоритете.");
            plugin.getLogger().info("[DEBUG] ==========================================");
            return;
        }

        Player p = event.getPlayer();
        String fullCommand = event.getMessage();
        String message = fullCommand.toLowerCase();

        if (plugin.getConfigManager() == null) {
            plugin.getLogger().severe("[DEBUG] ОШИБКА! ConfigManager равен null!");
            return;
        }

        List<String> restrictedCommands = plugin.getConfigManager().getCommandList();
        boolean debug = plugin.getConfigManager().getDebug();
        
        plugin.getLogger().info("[DEBUG] Debug режим: " + debug);
        plugin.getLogger().info("[DEBUG] ConfigManager не null: " + (plugin.getConfigManager() != null));

        if (restrictedCommands == null || restrictedCommands.isEmpty()) {
            if (debug) {
                plugin.getLogger().warning("[DEBUG] Список ограниченных команд пуст! Проверьте config.yml");
            }
            return;
        }

        // Извлекаем имя команды из полной команды
        String commandName = extractCommandName(fullCommand);

        if (debug) {
            plugin.getLogger().info("[DEBUG] ========== Проверка команды (HIGHEST приоритет) ==========");
            plugin.getLogger().info("[DEBUG] Игрок: " + p.getName());
            plugin.getLogger().info("[DEBUG] UUID: " + p.getUniqueId());
            plugin.getLogger().info("[DEBUG] Команда: " + fullCommand);
            plugin.getLogger().info("[DEBUG] Имя команды: " + commandName);
            plugin.getLogger().info("[DEBUG] Список ограниченных команд: " + restrictedCommands);
            plugin.getLogger().info("[DEBUG] Размер списка ограниченных команд: " + restrictedCommands.size());
            plugin.getLogger().info("[DEBUG] Список ActiveAdmins: " + plugin.getActiveAdmins());
            plugin.getLogger().info("[DEBUG] Размер списка ActiveAdmins: " + plugin.getActiveAdmins().size());
        }

        // Проверяем, является ли команда ограниченной
        boolean isRestrictedCommand = false;
        String matchedCommand = null;
        for (String cmd : restrictedCommands) {
            String cmdLower = cmd.toLowerCase();
            if (message.startsWith("/" + cmdLower + " ") || message.equals("/" + cmdLower)) {
                isRestrictedCommand = true;
                matchedCommand = cmd;
                break;
            }
        }

        if (debug) {
            plugin.getLogger().info("[DEBUG] Команда в списке ограниченных: " + isRestrictedCommand);
            if (matchedCommand != null) {
                plugin.getLogger().info("[DEBUG] Совпавшая команда из списка: " + matchedCommand);
            }
        }

        if (!isRestrictedCommand) {
            if (debug) {
                plugin.getLogger().info("[DEBUG] Команда '" + commandName + "' не в списке ограниченных. Пропуск проверки.");
                plugin.getLogger().info("[DEBUG] ======================================================");
            }
            return;
        }

        // Проверяем условия для разрешения команды
        String playerName = p.getName();
        boolean isInAdminMode = plugin.getActiveAdmins().contains(playerName);
        boolean hasIgnorePermission = p.hasPermission("kadminmode.ignore");

        if (debug) {
            plugin.getLogger().info("[DEBUG] Имя игрока для проверки: " + playerName);
            plugin.getLogger().info("[DEBUG] В режиме админа: " + isInAdminMode);
            plugin.getLogger().info("[DEBUG] Право kadminmode.ignore: " + hasIgnorePermission);
            plugin.getLogger().info("[DEBUG] Размер списка ActiveAdmins: " + plugin.getActiveAdmins().size());
        }

        // Разрешаем команду только если:
        // 1. Игрок в режиме админа
        // 2. Игрок имеет право игнорировать проверку
        if (isInAdminMode || hasIgnorePermission) {
            if (debug) {
                String reason = isInAdminMode ? "в режиме админа" : "имеет право kadminmode.ignore";
                plugin.getLogger().info("[DEBUG] Команда РАЗРЕШЕНА. Причина: " + reason);
                plugin.getLogger().info("[DEBUG] ======================================================");
            }
            return;
        }

        if (debug) {
            plugin.getLogger().info("[DEBUG] Команда ЗАБЛОКИРОВАНА. Игрок не в режиме админа и не имеет права kadminmode.ignore.");
            plugin.getLogger().info("[DEBUG] Примечание: Права на команду игнорируются - требуется режим админа.");
            plugin.getLogger().info("[DEBUG] Отменяем событие...");
        }
        
        event.setCancelled(true);
        
        send(p, plugin.getMessagesManager().notinmode());
        
        // Дополнительная проверка - убеждаемся, что событие действительно отменено
        if (debug) {
            plugin.getLogger().info("[DEBUG] Событие отменено: " + event.isCancelled());
            if (!event.isCancelled()) {
                plugin.getLogger().severe("[DEBUG] ОШИБКА! Событие не было отменено! Возможно, другой плагин перехватил его.");
            }
            plugin.getLogger().info("[DEBUG] ======================================================");
        }
    }

    /**
     * Извлекает имя команды из полной строки команды
     * Например: "/mute player reason" -> "mute"
     */
    private String extractCommandName(String fullCommand) {
        if (fullCommand == null || fullCommand.isEmpty() || !fullCommand.startsWith("/")) {
            return "";
        }
        String withoutSlash = fullCommand.substring(1);
        int spaceIndex = withoutSlash.indexOf(' ');
        if (spaceIndex == -1) {
            return withoutSlash.toLowerCase();
        }
        return withoutSlash.substring(0, spaceIndex).toLowerCase();
    }

    private void send(CommandSender sender, String msg) {
        sender.sendMessage(plugin.getConfigManager().COLORIZER.colorize(msg));
    }
}
