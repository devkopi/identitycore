package org.ccoding.identitycore.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.MessageUtils;
import java.util.Arrays;
import java.util.UUID;
import org.ccoding.identitycore.utils.PermissionUtils;

public class AdvancedEditorMenu {

    private final Player player;
    private String currentNick;

    public AdvancedEditorMenu(Player player) {
        this.player = player;
        this.currentNick = getCurrentNickForEditing();
    }

    public void openAdvancedEditor() {

        // Verificar permiso antes de abrir el menú
        if (!PermissionUtils.canUseAdvancedMenu(player)){
            player.sendMessage(org.ccoding.identitycore.utils.MessageUtils.formatColors("&cNo tienes permiso para usar el editor avanzado."));
            return;
        }

        Inventory menu = Bukkit.createInventory(null, 54,
                MessageUtils.formatColors("&8Editor Avanzado de Nick"));

        setupBaseLayout(menu);
        setupTextDisplay(menu);
        setupControls(menu);
        setupCloseButton(menu);

        player.openInventory(menu);
    }

    private void setupBaseLayout(Inventory menu) {
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                menu.setItem(i, border);
            }
        }
    }


    private void setupTextDisplay(Inventory menu) {
        String currentNick = getCurrentNickForEditing();
        String coloredNick = IdentityCore.getInstance().getNickManager().getColoredDisplayName(player);

        // Ítem principal con preview del nick
        ItemStack previewItem = createItem(Material.NAME_TAG,
                "&6Tu Nick",
                Arrays.asList("&f" + coloredNick,
                        "&7Click para editar colores en el chat",
                        "&8Ejemplo: &a&aPe&b&bpe&c&c12"));
        menu.setItem(22, previewItem);

        // Ítem de instrucciones actualizado
        ItemStack instructions = createItem(Material.BOOK,
                "&6Instrucciones",
                Arrays.asList("&e1. &7Copia tu nombre EXACTO actual:",
                        "&7   • Si usaste &f/nick <nombre>&7:",
                        "&7     copia ese mismo nombre que pusiste",
                        "&7   • Si NO usaste &f/nick&7:",
                        "&7     copia tu nombre real de Minecraft",
                        "&e2. &7Click en 'Editar Colores en Chat'",
                        "&e3. &7Pega tu nombre en el chat y añade los colores",
                        "&7   Ejemplo: &n&eccoding",
                        "&e4. &7Click en 'Aplicar Cambios'",
                        "",
                        "&c✖ &7Para cancelar escribe: &fcancelar"));
        menu.setItem(31, instructions);
    }

    private void setupControls(Inventory menu) {
        ItemStack editBtn = createItem(Material.WRITABLE_BOOK,
                "&eEditar Formatos en Chat",
                Arrays.asList("&7Abre el chat para aplicar formatos",
                        "&7a tu nombre actual",
                        "",
                        "&eFormatos disponibles:",
                        "&l&lL &7- Negrita (&l&lTexto&r)",
                        "&n&nN &7- Subrayado (&n&nTexto&r)",
                        "&o&oO &7- Itálica (&o&oTexto&r)",
                        "&m&M &7- Tachado (&m&mTexto&r)",
                        "&k&kK &7- Mágico (&k&kTexto&r)",
                        "",
                        "&7Ejemplo: &l&6Pe&n&6pe&o&612",
                        "",
                        "&c✖ &7Escribe &fcancelar &7para salir"));
        menu.setItem(29, editBtn);

        ItemStack applyBtn = createItem(Material.LIME_DYE,
                "&aAplicar Cambios",
                Arrays.asList("&7Guarda los colores",
                        "&7actuales en tu nick"));
        menu.setItem(33, applyBtn);
    }

    public void openColorEditor() {
        player.closeInventory();

        // Crear conversación
        org.bukkit.conversations.ConversationFactory factory =
                new org.bukkit.conversations.ConversationFactory(IdentityCore.getInstance())
                        .withFirstPrompt(new ColorEditPrompt(this))
                        .withLocalEcho(false) // No repetir mensaje en chat
                        .withEscapeSequence("cancelar")
                        .withTimeout(30)
                        .thatExcludesNonPlayersWithMessage("Solo jugadores pueden usar esto");

        factory.buildConversation(player).begin();
    }

    private void setupCloseButton(Inventory menu) {
        ItemStack closeBtn = createItem(Material.BARRIER, "&cCerrar Editor",
                Arrays.asList("&7Cerrar este menú"));
        menu.setItem(49, closeBtn);
    }


    public String getCurrentNick() {
        return currentNick;
    }

    public String getCurrentNickForEditing() {
        return IdentityCore.getInstance().getNickManager().getDisplayName(player);
    }

    public Player getPlayer() {
        return player;
    }

    public void processColorInput(String coloredInput) {
        UUID uuid = player.getUniqueId();

        // LIMPIAR colores únicos del menú básico para evitar conflictos
        IdentityCore.getInstance().getNickManager().getPlayerColors().remove(uuid);
        IdentityCore.getInstance().getNickManager().getPlayerFormats().remove(uuid);

        // Guardar el nick con colores
        IdentityCore.getInstance().getNickManager().setAdvancedNick(player, coloredInput);

        // Actualizar tab
        org.ccoding.identitycore.modules.tab.TabModule tabModule =
                (org.ccoding.identitycore.modules.tab.TabModule) IdentityCore.getInstance().getModule("TabModule");
        if (tabModule != null && tabModule.isEnabled()) {
            tabModule.updateAllPlayersTab();
        }
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

    // Para refrescar el menú
    public void refresh() {
        openAdvancedEditor();
    }
}