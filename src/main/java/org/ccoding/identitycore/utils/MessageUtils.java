package org.ccoding.identitycore.utils;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.ccoding.identitycore.IdentityCore;
import org.eclipse.aether.util.listener.ChainedTransferListener;


public class MessageUtils {

    private static IdentityCore plugin = IdentityCore.getInstance();

    public static String getMessage(String key) {

        FileConfiguration config = plugin.getConfig();

        String message = config.getString("messages." + key,
                "&cMensaje no enctrado: " + key);

        String prefix = config.getString("prefix", "");

        return ChatColor.translateAlternateColorCodes('&',
                prefix + message);
    }

    public static String getMessage(String key, String placeholder, String value) {

        String message = getMessage(key);
        return message.replace(placeholder, value);
    }

    public static void sendMessage(org.bukkit.command.CommandSender sender, String key) {
        sender.sendMessage(getMessage(key));
    }

    public static void sendMessage(org.bukkit.command.CommandSender sender, String key, String placeholder, String value) {
        sender.sendMessage(getMessage(key, placeholder, value));
    }

    // Formatear colores en strings normales
    public static String formatColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}