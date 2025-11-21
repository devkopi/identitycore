package org.ccoding.identitycore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ccoding.identitycore.menus.AdvancedEditorMenu;
import org.ccoding.identitycore.utils.MessageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdvancedEditorListener implements Listener {

    private final Map<UUID, AdvancedEditorMenu> playerEditors;

    public AdvancedEditorListener() {
        this.playerEditors = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) return;

        // Verificar si es el menú de editor avanzado
        if (inventory.getHolder() == null &&
                event.getView().getTitle().equals(MessageUtils.formatColors("&8Editor Avanzado de Nick"))) {

            event.setCancelled(true);
            AdvancedEditorMenu editor = playerEditors.get(player.getUniqueId());

            if (editor == null) {
                editor = new AdvancedEditorMenu(player);
                playerEditors.put(player.getUniqueId(), editor);
            }

            handleAdvancedEditorClick(event, editor, player);
        }
    }

    private void handleAdvancedEditorClick(InventoryClickEvent event, AdvancedEditorMenu editor, Player player) {
        int slot = event.getSlot();

        // Botón Volver
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        // Botón Cerrar
        if (slot == 53) {
            player.closeInventory();
            return;
        }

        // Botón Editar Colores en Chat
        if (slot == 29) {
            editor.openColorEditor();
            return;
        }

        // Botón Aplicar Cambios
        if (slot == 33) {
            player.sendMessage(MessageUtils.formatColors("&aCambios aplicados!"));
            return;
        }

        // Paleta de colores (slots 10-16) - OPCIONAL: puedes quitar esto
        if (slot >= 10 && slot <= 16) {
            player.sendMessage(MessageUtils.formatColors("&eUsa el botón 'Editar Colores' para personalizar."));
            return;
        }

    }

    public void registerEditor(Player player, AdvancedEditorMenu editor) {
        playerEditors.put(player.getUniqueId(), editor);
    }
}