package org.ccoding.identitycore.modules.chat;

import org.ccoding.identitycore.modules.Module;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

// Nuevos imports para el sistema de chat moderno
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.TextComponent;

public class ChatModule implements Module, Listener {

    private final IdentityCore plugin;
    private boolean enabled = false;

    public ChatModule(IdentityCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
        enabled = true;
        plugin.getLogger().info("ChatModule activado");
    }

    @Override
    public void disable() {
        enabled = false;
        plugin.getLogger().info("ChatModule desactivado");
    }

    @Override
    public String getName() {
        return "ChatModule";
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        if (!enabled) return;

        var player = event.getPlayer();
        String originalName = player.getName();
        String displayName = plugin.getNickManager().getDisplayName(player);

        plugin.getLogger().info("CHAT EVENT CAPTURADO: " + originalName + " -> " + displayName);

        // Solo modificar si tiene nick diferente
        if (!originalName.equals(displayName)) {
            // Obtener el mensaje original
            Component originalMessage = event.message();
            String messageText = PlainTextComponentSerializer.plainText().serialize(originalMessage);

            plugin.getLogger().info("Mensaje: " + messageText);

            // CREAR NUEVO FORMATO DE CHAT CON EL NICK
            // Formato simple: <displayName> message
            String formattedChat = "&7<&f" + displayName + "&7> &f" + messageText;

            // Convertir el string formateado a Component
            Component formattedComponent = Component.text(MessageUtils.formatColors(formattedChat));

            // Establecer el nuevo mensaje formateado
            event.renderer((source, sourceDisplayName, message, viewer) -> formattedComponent);

            plugin.getLogger().info("NICK APLICADO EN CHAT: " + originalName + " → " + displayName);
        } else {
            plugin.getLogger().info("No se aplica nick (mismo nombre)");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}