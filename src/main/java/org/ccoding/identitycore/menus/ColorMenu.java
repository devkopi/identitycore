package org.ccoding.identitycore.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.ColorUtils;
import org.ccoding.identitycore.utils.MessageUtils;

import java.util.Arrays;
import java.util.Map;

public class ColorMenu {

    private final Player player;
    private String currentColor = "";
    private String currentFormat = "";

    public ColorMenu(Player player) {
        this.player = player;
    }

    public void openColorMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtils.formatColors("&8Personalizar Nick - Colores"));
        fillBorders(menu, Material.BLACK_STAINED_GLASS_PANE);
        fillHeaderFooter(menu);
        setupNavigation(menu);
        setupColors(menu);
        setupPreview(menu);
        setupAdvancedButton(menu);
        setupCloseButton(menu);
        player.openInventory(menu);
    }

    public void openFormatMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtils.formatColors("&8Personalizar Nick - Formatos"));
        fillBorders(menu, Material.BLACK_STAINED_GLASS_PANE);
        fillHeaderFooter(menu);
        setupNavigation(menu);
        setupFormats(menu);
        setupPreview(menu);
        setupCloseButton(menu);
        player.openInventory(menu);
    }

    private void fillBorders(Inventory menu, Material material) {
        for (int i = 0; i < 54; i += 9) {
            menu.setItem(i, createItem(material, " "));
            menu.setItem(i + 8, createItem(material, " "));
        }
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, createItem(material, " "));
            menu.setItem(i + 45, createItem(material, " "));
        }
    }

    private void fillHeaderFooter(Inventory menu) {
        ItemStack header = createItem(Material.BLACK_STAINED_GLASS_PANE, "&8Personalizar Tu Nick");
        ItemStack footer = createItem(Material.BLACK_STAINED_GLASS_PANE, "&8Selecciona un estilo");

        for (int i = 9; i <= 17; i++) {
            if (i != 10 && i != 16) {
                menu.setItem(i, header);
            }
        }
        for (int i = 45; i <= 53; i++) {
            if (i != 49) {
                menu.setItem(i, footer);
            }
        }
    }

    private void setupNavigation(Inventory menu) {
        ItemStack colorsBtn = createItem(Material.PLAYER_HEAD, "&6Colores",
                Arrays.asList("&7Haz click para ver", "&7todos los colores disponibles"));
        menu.setItem(10, colorsBtn);

        ItemStack formatsBtn = createItem(Material.PLAYER_HEAD, "&6Formatos",
                Arrays.asList("&7Haz click para ver", "&7todos los formatos disponibles"));
        menu.setItem(16, formatsBtn);
    }

    private void setupColors(Inventory menu) {
        int slot = 19;
        int column = 0;

        for (ColorUtils.ColorData color : ColorUtils.COLORS.values()) {
            if (slot > 43) break;

            Material material = getSafeMaterial(color.getMaterial(), Material.WHITE_WOOL);

            if (ColorUtils.hasColorPermission(player, getKeyFromColorData(color))) {
                ItemStack colorItem = createItem(material,
                        color.getCode() + color.getName(),
                        Arrays.asList("&7Código: " + color.getCode(),
                                "&7Haz click para seleccionar"));
                menu.setItem(slot, colorItem);
            } else {
                ItemStack lockedItem = createItem(Material.BARRIER,
                        "&8" + color.getName(),
                        Arrays.asList("&cNo tienes permiso", "&cpara usar este color"));
                menu.setItem(slot, lockedItem);
            }

            slot++;
            column++;

            if (column % 7 == 0) {
                slot += 2;
            }
        }
    }

    private void setupFormats(Inventory menu) {
        int slot = 19;
        int column = 0;

        for (ColorUtils.ColorData format : ColorUtils.FORMATS.values()) {
            if (slot > 43) break;

            if (ColorUtils.hasFormatPermission(player, getKeyFromFormatData(format))) {
                ItemStack formatItem = createItem(Material.PAPER,
                        format.getCode() + format.getName(),
                        Arrays.asList("&7Código: " + format.getCode(),
                                "&7Haz click para seleccionar"));
                menu.setItem(slot, formatItem);
            } else {
                ItemStack lockedItem = createItem(Material.BARRIER,
                        "&8" + format.getName(),
                        Arrays.asList("&cNo tienes permiso", "&cpara usar este formato"));
                menu.setItem(slot, lockedItem);
            }

            slot++;
            column++;

            if (column % 7 == 0) {
                slot += 2;
            }
        }
    }

    private void setupPreview(Inventory menu) {
        String currentNick = IdentityCore.getInstance().getNickManager().getCurrentNick(player);
        String displayName = (currentNick != null && !currentNick.isEmpty()) ? currentNick : player.getName();

        String preview = ColorUtils.generatePreview(displayName, currentColor, currentFormat);
        ItemStack previewItem = createItem(Material.NAME_TAG, preview,
                Arrays.asList("&7Este es cómo se verá", "&7tu nick con los estilos", "&7seleccionados"));
        menu.setItem(4, previewItem);
    }

    /**
     * Configura el botón de editor avanzado
     */
    private void setupAdvancedButton(Inventory menu) {
        if (player.hasPermission("identitycore.advanced")) {
            ItemStack advancedBtn = createItem(Material.WRITABLE_BOOK, "&6Editor Avanzado",
                    Arrays.asList("&7Haz click para abrir",
                            "&7el editor avanzado",
                            "&r",
                            "&c⚠ &eAdvertencia: &7Al usar colores avanzados,",
                            "&7el color único del menú básico se desactivará"));
            menu.setItem(53, advancedBtn);
        }
    }


    private void setupCloseButton(Inventory menu) {
        ItemStack closeBtn = createItem(Material.BARRIER, "&cCerrar Menu",
                Arrays.asList("&7Haz click para cerrar", "&7este menu"));
        menu.setItem(49, closeBtn);
    }

    private ItemStack createItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.formatColors(name));
        if (lore != null) {
            meta.setLore(lore.stream().map(MessageUtils::formatColors).toList());
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material material, String name) {
        return createItem(material, name, null);
    }

    private Material getSafeMaterial(String materialName, Material defaultMaterial) {
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("[IdentityCore] Material invalido: " + materialName + ", usando: " + defaultMaterial);
            return defaultMaterial;
        }
    }

    private String getKeyFromColorData(ColorUtils.ColorData colorData) {
        for (Map.Entry<String, ColorUtils.ColorData> entry : ColorUtils.COLORS.entrySet()) {
            if (entry.getValue().equals(colorData)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private String getKeyFromFormatData(ColorUtils.ColorData formatData) {
        for (Map.Entry<String, ColorUtils.ColorData> entry : ColorUtils.FORMATS.entrySet()) {
            if (entry.getValue().equals(formatData)) {
                return entry.getKey();
            }
        }
        return "";
    }

    public String getCurrentColor() { return currentColor; }
    public void setCurrentColor(String color) { this.currentColor = color; }

    public String getCurrentFormat() { return currentFormat; }
    public void setCurrentFormat(String format) { this.currentFormat = format; }
}