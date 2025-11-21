package org.ccoding.identitycore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.MessageUtils;
import org.ccoding.identitycore.utils.PermissionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IdentityCoreCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("identitycore.help")) {
            MessageUtils.sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                break;

            case "reload":
                reloadConfig(sender);
                break;

            default:
                sendUnknownCommand(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m-------------------§r §6§lIdentityCore §8§m-------------------");

        sender.sendMessage(" §8▪ §7/identitycore help §8- §fMostrar esta ayuda");

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (PermissionUtils.canUseNick(player)) {
                sender.sendMessage(" §8▪ §7/nick <nombre> §8- §fCambiar tu nick");
                sender.sendMessage(" §8▪ §7/nick restore §8- §fRestaurar nombre original");

                if (PermissionUtils.canUseBasicMenu(player)) {
                    sender.sendMessage(" §8▪ §7/nick settings §8- §eMenú de colores básicos");
                }

                if (PermissionUtils.canUseAdvancedMenu(player)) {
                    sender.sendMessage(" §8▪ §7/nick advanced §8- §6Editor avanzado");
                }
            }

            if (PermissionUtils.canReload(player)) {
                sender.sendMessage(" §8▪ §7/identitycore reload §8- §cRecargar configuración");
            }
        } else {
            sender.sendMessage(" §8▪ §7/nick <nombre> §8- §fCambiar nick de jugador");
            sender.sendMessage(" §8▪ §7/nick restore §8- §fRestaurar nombre original");
            sender.sendMessage(" §8▪ §7/nick settings §8- §eMenú de colores básicos");
            sender.sendMessage(" §8▪ §7/nick advanced §8- §6Editor avanzado");
            sender.sendMessage(" §8▪ §7/identitycore reload §8- §cRecargar configuración");
        }

        sender.sendMessage("§8§m------------------------------------------------");
        sender.sendMessage("§7Versión: §f1.0");
    }

    private void reloadConfig(CommandSender sender) {
        // Verificar si es jugador y tiene permiso, o si es consola
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!PermissionUtils.canReload(player)) {
                MessageUtils.sendMessage(sender, "no-permission");
                return;
            }
        }

        // La consola siempre puede recargar

        try {
            IdentityCore.getInstance().reloadConfig();
            MessageUtils.sendMessage(sender, "reload-success");
        } catch (Exception e) {
            sender.sendMessage("§cError al recargar la configuración: " + e.getMessage());
        }
    }

    private void sendUnknownCommand(CommandSender sender) {
        sender.sendMessage("§cComando desconocido. Usa §f/identitycore help §cpara ver la ayuda.");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (!(sender instanceof Player)) return suggestions;

        Player player = (Player) sender;
        if (!PermissionUtils.canUseNick(player)) return suggestions;

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            if ("help".startsWith(partial)) suggestions.add("help");

            if (PermissionUtils.canReload(player) && "reload".startsWith(partial)) {
                suggestions.add("reload");
            }
        }

        return suggestions;
    }
}