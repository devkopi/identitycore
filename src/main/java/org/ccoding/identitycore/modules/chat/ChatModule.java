package org.ccoding.identitycore.modules.chat;

import org.ccoding.identitycore.modules.Module;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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
        String displayName = plugin.getNickManager().getColoredDisplayName(player);

        if (!originalName.equals(displayName)) {
            Component originalMessage = event.message();
            String messageText = PlainTextComponentSerializer.plainText().serialize(originalMessage);

            String formattedChat = "&7<&f" + displayName + "&7> &f" + messageText;
            Component formattedComponent = Component.text(MessageUtils.formatColors(formattedChat));

            event.renderer((source, sourceDisplayName, message, viewer) -> formattedComponent);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}