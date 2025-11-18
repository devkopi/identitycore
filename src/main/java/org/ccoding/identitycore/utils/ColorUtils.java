package org.ccoding.identitycore.utils;

import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;

public class ColorUtils {

    public static class ColorData {
        private final String code;
        private final String name;
        private final String permission;
        private final String material;

        public ColorData(String code, String name, String permission, String material) {
            this.code = code;
            this.name = name;
            this.permission = permission;
            this.material = material;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getPermission() { return permission; }
        public String getMaterial() { return material; }
    }

    public static final Map<String, ColorData> COLORS = new HashMap<>();
    public static final Map<String, ColorData> FORMATS = new HashMap<>();

    static {
        // Colores con materiales reales de Minecraft
        COLORS.put("black", new ColorData("&0", "Negro", "identitycore.color.black", "BLACK_WOOL"));
        COLORS.put("dark_blue", new ColorData("&1", "Azul Oscuro", "identitycore.color.dark_blue", "BLUE_WOOL"));
        COLORS.put("dark_green", new ColorData("&2", "Verde Oscuro", "identitycore.color.dark_green", "GREEN_WOOL"));
        COLORS.put("dark_aqua", new ColorData("&3", "Aqua Oscuro", "identitycore.color.dark_aqua", "CYAN_WOOL"));
        COLORS.put("dark_red", new ColorData("&4", "Rojo Oscuro", "identitycore.color.dark_red", "RED_WOOL"));
        COLORS.put("dark_purple", new ColorData("&5", "Morado Oscuro", "identitycore.color.dark_purple", "PURPLE_WOOL"));
        COLORS.put("gold", new ColorData("&6", "Oro", "identitycore.color.gold", "ORANGE_WOOL"));
        COLORS.put("gray", new ColorData("&7", "Gris", "identitycore.color.gray", "LIGHT_GRAY_WOOL"));
        COLORS.put("dark_gray", new ColorData("&8", "Gris Oscuro", "identitycore.color.dark_gray", "GRAY_WOOL"));
        COLORS.put("blue", new ColorData("&9", "Azul", "identitycore.color.blue", "LIGHT_BLUE_WOOL"));
        COLORS.put("green", new ColorData("&a", "Verde", "identitycore.color.green", "LIME_WOOL"));
        COLORS.put("aqua", new ColorData("&b", "Aqua", "identitycore.color.aqua", "LIGHT_BLUE_WOOL"));
        COLORS.put("red", new ColorData("&c", "Rojo", "identitycore.color.red", "PINK_WOOL"));
        COLORS.put("light_purple", new ColorData("&d", "Morado Claro", "identitycore.color.light_purple", "MAGENTA_WOOL"));
        COLORS.put("yellow", new ColorData("&e", "Amarillo", "identitycore.color.yellow", "YELLOW_WOOL"));
        COLORS.put("white", new ColorData("&f", "Blanco", "identitycore.color.white", "WHITE_WOOL"));

        // Formatos
        FORMATS.put("bold", new ColorData("&l", "Negrita", "identitycore.format.bold", "PAPER"));
        FORMATS.put("italic", new ColorData("&o", "Itálica", "identitycore.format.italic", "PAPER"));
        FORMATS.put("underline", new ColorData("&n", "Subrayado", "identitycore.format.underline", "PAPER"));
        FORMATS.put("strikethrough", new ColorData("&m", "Tachado", "identitycore.format.strikethrough", "PAPER"));
        FORMATS.put("magic", new ColorData("&k", "Mágico", "identitycore.format.magic", "PAPER"));
        FORMATS.put("reset", new ColorData("&r", "Resetear", "identitycore.format.reset", "PAPER"));
    }

    public static String generatePreview(String nick, String colorCode, String formatCode) {
        String coloredNick = colorCode + formatCode + nick;
        return MessageUtils.formatColors("&7Preview: " + coloredNick);
    }

    public static boolean hasColorPermission(org.bukkit.entity.Player player, String colorKey) {
        ColorData colorData = COLORS.get(colorKey);
        if (colorData != null) {
            return player.hasPermission(colorData.getPermission());
        }
        return false;
    }

    public static boolean hasFormatPermission(org.bukkit.entity.Player player, String formatKey) {
        ColorData formatData = FORMATS.get(formatKey);
        if (formatData != null) {
            return player.hasPermission(formatData.getPermission());
        }
        return false;
    }
}