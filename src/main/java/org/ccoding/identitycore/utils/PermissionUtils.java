package org.ccoding.identitycore.utils;

import org.bukkit.entity.Player;

public class PermissionUtils {

    // Permisos maestros
    public static final String ALL = "identitycore.*";
    public static final String ALL_COLORS = "identitycore.color.*";
    public static final String ALL_FORMATS = "identitycore.format.*";
    public static final String ALL_COMMANDS = "identitycore.command.*";

    // Comandos
    public static final String COMMAND_HELP = "identitycore.command.help";
    public static final String COMMAND_NICK = "identitycore.command.nick";

    // Menú avanzado
    public static final String MENU_BASIC = "identitycore.menu.basic";
    public static final String ADVANCED = "identitycore.advanced";

    // Admin
    public static final String ADMIN = "identitycore.admin";
    public static final String ADMIN_RELOAD = "identitycore.admin.reload";

    /**
     * Verifica si un jugador tiene un permiso específico
     */
    public static boolean has(Player player, String permission) {
        return player != null && player.hasPermission(permission);
    }

    /**
     * Verifica si puede usar el comando /nick
     */
    public static boolean canUseNick(Player player) {
        return has(player, ALL) || has(player, COMMAND_NICK);
    }

    /**
     * Verifica si puede acceder al menú básico (/nick settings)
     */
    public static boolean canUseBasicMenu(Player player) {
        return has(player, ALL) || has(player, "identitycore.menu.basic") || canUseNick(player);
    }

    /**
     * Verifica si puede acceder al menú avanzado (/nick advanced)
     */
    public static boolean canUseAdvancedMenu(Player player) {
        return has(player, ALL) || has(player, ADVANCED);
    }

    /**
     * Verifica permiso para colores específicos
     */
    public static boolean hasColorPermission(Player player, String colorKey) {
        if (has(player, ALL) || has(player, ALL_COLORS)) {
            return true;
        }

        ColorUtils.ColorData colorData = ColorUtils.COLORS.get(colorKey);
        if (colorData != null) {
            return has(player, colorData.getPermission());
        }

        return false;
    }

    /**
     * Verifica permiso para formatos específicos
     */
    public static boolean hasFormatPermission(Player player, String formatKey) {
        if (has(player, ALL) || has(player, ALL_FORMATS)) {
            return true;
        }

        ColorUtils.ColorData formatData = ColorUtils.FORMATS.get(formatKey);
        if (formatData != null) {
            return has(player, formatData.getPermission());
        }

        return false;
    }

    /**
     * Verifica si puede recargar el plugin
     */
    public static boolean canReload(Player player) {
        return has(player, ALL) || has(player, ADMIN_RELOAD);
    }

    /**
     * Obtiene todos los colores disponibles para un jugador
     */
    public static java.util.Map<String, ColorUtils.ColorData> getAvailableColors(Player player) {
        java.util.Map<String, ColorUtils.ColorData> availableColors = new java.util.HashMap<>();

        for (java.util.Map.Entry<String, ColorUtils.ColorData> entry : ColorUtils.COLORS.entrySet()) {
            if (hasColorPermission(player, entry.getKey())) {
                availableColors.put(entry.getKey(), entry.getValue());
            }
        }

        return availableColors;
    }

    /**
     * Obtiene todos los formatos disponibles para un jugador
     */
    public static java.util.Map<String, ColorUtils.ColorData> getAvailableFormats(Player player) {
        java.util.Map<String, ColorUtils.ColorData> availableFormats = new java.util.HashMap<>();

        for (java.util.Map.Entry<String, ColorUtils.ColorData> entry : ColorUtils.FORMATS.entrySet()) {
            if (hasFormatPermission(player, entry.getKey())) {
                availableFormats.put(entry.getKey(), entry.getValue());
            }
        }

        return availableFormats;
    }
}