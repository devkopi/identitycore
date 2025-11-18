package org.ccoding.identitycore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.menus.ColorMenu;
import org.ccoding.identitycore.utils.ColorUtils;
import org.ccoding.identitycore.utils.MessageUtils;

public class ColorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isColorMenu(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();
        String strippedName = org.bukkit.ChatColor.stripColor(displayName);

        processMenuClick(player, strippedName, event.getSlot());
    }

    private boolean isColorMenu(String title) {
        return title.contains("Personalizar Nick");
    }

    private void processMenuClick(Player player, String itemName, int slot) {
        switch (slot) {
            case 10:
                openColorMenu(player);
                break;
            case 16:
                openFormatMenu(player);
                break;
            case 49:
                player.closeInventory();
                player.sendMessage(MessageUtils.formatColors("&cMenú cerrado"));
                break;
            default:
                processColorOrFormatSelection(player, itemName, slot);
                break;
        }
    }

    private void processColorOrFormatSelection(Player player, String itemName, int slot) {
        ColorUtils.ColorData selectedColor = findColorByDisplayName(itemName);
        ColorUtils.ColorData selectedFormat = findFormatByDisplayName(itemName);

        if (selectedColor != null) {
            applyColorSelection(player, selectedColor);
        } else if (selectedFormat != null) {
            applyFormatSelection(player, selectedFormat);
        }
    }

    private void applyColorSelection(Player player, ColorUtils.ColorData color) {
        if (!ColorUtils.hasColorPermission(player, getKeyFromColorData(color))) {
            player.sendMessage(MessageUtils.formatColors("&cNo tienes permiso para usar este color"));
            return;
        }

        // Guardar en NickManager
        IdentityCore.getInstance().getNickManager().applyColorConfig(player, color.getCode(), null);

        // Actualizar módulos
        updatePlayerDisplay(player);

        player.sendMessage(MessageUtils.formatColors("&aColor aplicado: " + color.getCode() + color.getName()));

        String preview = ColorUtils.generatePreview(player.getName(), color.getCode(), "");
        player.sendMessage(preview);
    }

    private void applyFormatSelection(Player player, ColorUtils.ColorData format) {
        if (!ColorUtils.hasFormatPermission(player, getKeyFromFormatData(format))) {
            player.sendMessage(MessageUtils.formatColors("&cNo tienes permiso para usar este formato"));
            return;
        }

        // Guardar en NickManager
        IdentityCore.getInstance().getNickManager().applyColorConfig(player, null, format.getCode());

        // Actualizar módulos
        updatePlayerDisplay(player);

        player.sendMessage(MessageUtils.formatColors("&aFormato aplicado: " + format.getCode() + format.getName()));

        String preview = ColorUtils.generatePreview(player.getName(), "", format.getCode());
        player.sendMessage(preview);
    }

    // Actualizar ChatModule y TabModule
    private void updatePlayerDisplay(Player player) {
        try {
            // Actualizar TabModule
            org.ccoding.identitycore.modules.tab.TabModule tabModule =
                    (org.ccoding.identitycore.modules.tab.TabModule) IdentityCore.getInstance().getModule("TabModule");
            if (tabModule != null && tabModule.isEnabled()) {
                tabModule.updatePlayerTabName(player);
            }

            // El ChatModule se actualiza automáticamente en el próximo mensaje
        } catch (Exception e) {
            IdentityCore.getInstance().getLogger().warning("Error actualizando modulos: " + e.getMessage());
        }
    }

    private ColorUtils.ColorData findColorByDisplayName(String displayName) {
        String strippedName = org.bukkit.ChatColor.stripColor(displayName);
        for (ColorUtils.ColorData color : ColorUtils.COLORS.values()) {
            if (color.getName().equals(strippedName)) {
                return color;
            }
        }
        return null;
    }

    private ColorUtils.ColorData findFormatByDisplayName(String displayName) {
        String strippedName = org.bukkit.ChatColor.stripColor(displayName);
        for (ColorUtils.ColorData format : ColorUtils.FORMATS.values()) {
            if (format.getName().equals(strippedName)) {
                return format;
            }
        }
        return null;
    }

    private String getKeyFromColorData(ColorUtils.ColorData colorData) {
        for (java.util.Map.Entry<String, ColorUtils.ColorData> entry : ColorUtils.COLORS.entrySet()) {
            if (entry.getValue().equals(colorData)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private String getKeyFromFormatData(ColorUtils.ColorData formatData) {
        for (java.util.Map.Entry<String, ColorUtils.ColorData> entry : ColorUtils.FORMATS.entrySet()) {
            if (entry.getValue().equals(formatData)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private void openColorMenu(Player player) {
        ColorMenu colorMenu = new ColorMenu(player);
        colorMenu.openColorMenu();
    }

    private void openFormatMenu(Player player) {
        ColorMenu colorMenu = new ColorMenu(player);
        colorMenu.openFormatMenu();
    }
}