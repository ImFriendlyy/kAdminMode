package vv0ta3fa9.plugin.kAdminMode.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import vv0ta3fa9.plugin.kAdminMode.kAdminMode;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final kAdminMode plugin;

    public CommandManager(kAdminMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            toggle(sender);
            return true;
        }
        if (!sender.hasPermission("kadminmode.admin")) {
            send(sender, plugin.getMessagesManager().nopermission());
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "switch":
                toggle(sender);

                break;
            case "list":
                if (!sender.hasPermission("kadminmode.ignore")) {
                    send(sender, plugin.getMessagesManager().nopermission());
                    return true;
                }
                if (args.length < 2) {
                    send(sender, "&cУкажите подкоманду: active/notactive/all");
                    return true;
                }

                String subcommandlist = args[1].toLowerCase();

                switch (subcommandlist) {
                    case "active":
                        send(sender, plugin.getActiveAdmins().toString());
                        break;
                    case "notactive":
                        send(sender, plugin.getNotActiveAdmins().toString());
                        break;
                    case "all":
                        sender.sendMessage(plugin.getConfigManager().COLORIZER.colorize
                                ("&eАдминистраторы &aв &eрежиме работы: &6" + plugin.getActiveAdmins()));
                        sender.sendMessage(plugin.getConfigManager().COLORIZER.colorize
                                ("&eАдминистраторы &cне &eв режиме работы: &6" + plugin.getNotActiveAdmins()));
                        break;
                }
//            case "reload":
//                 try {
//                     plugin.getServer().getPluginManager().disablePlugin(plugin);
//                     plugin.getServer().getPluginManager().enablePlugin(plugin);
//                     send(sender, plugin.getMessagesManager().reloadplugin());
//                 } catch (Exception e) {
//                     send(sender, "&ckAdminMode &7>> &c&lВнимание, ошибка перезагрузки плагина!!");
//                     e.printStackTrace();
//                 }
        }

        return true;
    }

    private void toggle(CommandSender sender) {
        String name = sender.getName();

        if (plugin.getActiveAdmins().contains(name)) {

            plugin.getActiveAdmins().remove(name);
            plugin.getNotActiveAdmins().add(name);
            send(sender, plugin.getMessagesManager().disableMode());

            if (plugin.getLogManager() != null) {
                plugin.getLogManager().writeLog(LocalDateTime.now().toString(), name, "disable");
            }
            plugin.getLogger().info("Администратор " + name + " вышел из режима работы.");

            for (Player onlineAdmin : Bukkit.getOnlinePlayers()) {
                if (!onlineAdmin.equals(sender) && plugin.getActiveAdmins().contains(onlineAdmin.getName())) {
                    onlineAdmin.sendMessage(
                            plugin.getConfigManager().COLORIZER.colorize(plugin.getMessagesManager().joined_admin(name)));
                }
            }
        } else if (!plugin.getActiveAdmins().contains(name)){
            plugin.getActiveAdmins().add(name);
            plugin.getNotActiveAdmins().remove(name);
            send(sender, plugin.getMessagesManager().enableMode());

            if (plugin.getLogManager() != null) {
                plugin.getLogManager().writeLog(LocalDateTime.now().toString(), name, "enable");
            }
            plugin.getLogger().info("Администратор " + name + " зашел в режим работы.");
        }
    }

    private void send(CommandSender sender, String msg) {
        sender.sendMessage(plugin.getConfigManager().COLORIZER.colorize(msg));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("kadminmode.ignore")) {
            return Arrays.asList("switch", "list");
        }
        if (args.length == 1) {
            return Arrays.asList("switch");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("list") && sender.hasPermission("kadminmode.ignore")) {
            return Arrays.asList("active", "notactive", "all");
        }

        return Collections.emptyList();
    }
}
