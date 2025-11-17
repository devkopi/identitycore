package org.ccoding.identitycore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ccoding.identitycore.utils.MessageUtils;
import org.jetbrains.annotations.NotNull;

public class IdentityCoreCommand implements CommandExecutor {

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
        sender.sendMessage("§6=== §eIdentityCore §6===");
        sender.sendMessage("§7/identitycore help §8- §fMuestra de comandos sin OP");

        if (sender.hasPermission("identitycore.admin")) {
            sender.sendMessage("§7/identitycore reload §8- §fRecarga la configuración");
        }

        if (sender.hasPermission("identitycore.nick")) {
            sender.sendMessage("§7/nick <nick> §8- §fCambia tu nick");
            sender.sendMessage("§7/nick restore §8- §fRestaura tu nombre original");
        }

        sender.sendMessage("§6Versión: 1.0");
    }

    private void reloadConfig(CommandSender sender) {
        if (!sender.hasPermission("identitycore.admin")) {
            MessageUtils.sendMessage(sender, "no-permission");
            return;
        }

        try {
            org.ccoding.identitycore.IdentityCore.getInstance().reloadConfig();
            MessageUtils.sendMessage(sender, "reload-success");
        } catch (Exception e) {
            sender.sendMessage("§cError al recargar la configuración: " + e.getMessage());
        }
    }

    private void sendUnknownCommand(CommandSender sender) {
        sender.sendMessage("§cComando desconocido. Usa §f/identitycore help §cpara ver la ayuda.");
    }
}