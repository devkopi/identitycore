package org.ccoding.identitycore.menus;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.MessageUtils;

public class ColorEditPrompt extends StringPrompt {

    private final AdvancedEditorMenu menu;

    public ColorEditPrompt(AdvancedEditorMenu menu) {
        this.menu = menu;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        String currentNick = menu.getCurrentNickForEditing();
        return MessageUtils.formatColors(
                "&6&lEDITOR DE COLORES\n" +
                        "&7Nick actual: &f" + currentNick + "\n" +
                        "&7Escribe el nuevo nick con colores:\n" +
                        "&8(Ejemplo: &a&aPe&b&bpe&c&c12&8)\n" +
                        "&eEscribe 'cancelar' para volver"
        );
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("cancelar")) {
            menu.getPlayer().sendMessage(MessageUtils.formatColors("&cEdición cancelada."));
        } else {
            // VALIDAR que el input contenga el nick completo
            String cleanInput = input.replaceAll("&[0-9a-fk-or]", ""); // Quitar códigos de color
            String expectedNick = menu.getCurrentNickForEditing();

            if (!cleanInput.equals(expectedNick)) {
                menu.getPlayer().sendMessage(MessageUtils.formatColors("&cError: Debes incluir tu nick completo: &e" + expectedNick));
                menu.getPlayer().sendMessage(MessageUtils.formatColors("&cLa operación ha sido cancelada."));

                // Reabrir menú directamente
                Bukkit.getScheduler().runTaskLater(IdentityCore.getInstance(), menu::openAdvancedEditor, 10L);

                return END_OF_CONVERSATION; // ← Cancela la conversación
            }

            // Si pasa validación, procesar
            menu.processColorInput(input);
            menu.getPlayer().sendMessage(MessageUtils.formatColors("&a¡Nick actualizado correctamente!"));
        }

        // Reabrir menú
        Bukkit.getScheduler().runTaskLater(IdentityCore.getInstance(), () -> {
            menu.openAdvancedEditor();
        }, 10L);

        return END_OF_CONVERSATION;
    }
}