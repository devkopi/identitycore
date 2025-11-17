package org.ccoding.identitycore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.MessageUtils;
import org.jetbrains.annotations.NotNull;

// Importamos el TabModule
import org.ccoding.identitycore.modules.tab.TabModule;

public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "only-players");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("identitycore.nick")) {
            MessageUtils.sendMessage(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendNickHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendNickHelp(player);
                break;

            case "restore":
                restoreNick(player);
                break;

            default:
                setNick(player, args[0]);
                break;
        }

        return true;
    }

    private void sendNickHelp(Player player) {
        player.sendMessage("§6=== §eNick Help §6===");
        player.sendMessage("§7/nick <nombre> §8- §fCambia tu nick");
        player.sendMessage("§7/nick restore §8- §fRestaura tu nombre original");
        player.sendMessage("§7/nick help §8- §fMuestra esta ayuda");
    }

    private void setNick(Player player, String nick) {
        if (nick.length() < 3) {
            MessageUtils.sendMessage(player, "nick-too-short");
            return;
        }

        if (nick.length() > 10) {
            MessageUtils.sendMessage(player, "nick-too-long");
            return;
        }

        if (!isValidNick(nick)) {
            MessageUtils.sendMessage(player, "nick-invalid");
            return;
        }

        IdentityCore.getInstance().getNickManager().setNick(player, nick);
        MessageUtils.sendMessage(player, "nick-changed", "%nick%", nick);

        // Actualizar TabModule
        updateTabModule(player);

        player.sendMessage("§7Ahora tu nombre aparecerá como: §e" + nick + " §7en el chat");
    }

    private void restoreNick(Player player) {
        IdentityCore.getInstance().getNickManager().restoreOriginalName(player);
        MessageUtils.sendMessage(player, "nick-restored", "%name%", player.getName());

        // Actualizar TabModule
        updateTabModule(player);

        player.sendMessage("§7Ahora tu nombre aparecerá como: §e" + player.getName() + " §7en el chat");
    }

    private void updateTabModule(Player player) {
        try {
            TabModule tabModule = (TabModule) IdentityCore.getInstance().getModule("TabModule");

            if (tabModule != null && tabModule.isEnabled()) {
                tabModule.updatePlayerTabName(player);
                IdentityCore.getInstance().getLogger().info("TabModule actualizado para: " + player.getName());
            }
        } catch (Exception e) {
            IdentityCore.getInstance().getLogger().warning("No se pudo actaulizar TabModule: " + e.getMessage());
        }
    }

    private boolean isValidNick(String nick) {
        return nick.matches("[a-zA-Z0-9_]+");
    }
}