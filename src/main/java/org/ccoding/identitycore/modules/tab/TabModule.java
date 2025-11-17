package org.ccoding.identitycore.modules.tab;

import org.ccoding.identitycore.modules.Module;
import org.ccoding.identitycore.IdentityCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TabModule implements Module, Listener {

    private final IdentityCore plugin;
    private boolean enabled = false;

    public TabModule(IdentityCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        enabled = true;
        plugin.getLogger().info("TabModule activado");

        updateAllPlayersTab();
    }

    @Override
    public void disable() {
        enabled = false;
        plugin.getLogger().info("TabModule desactivado");
        restoreAllPlayersTab();
    }

    @Override
    public String getName() {
        return "TabModule";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        updatePlayerTabName(player);
        updateAllPlayersTab();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Para futuras expansiones
        plugin.getLogger().info("TabModule - PlayerQuitEvent: " + event.getPlayer().getName());
    }

    public void updatePlayerTabName(Player player) {
        if (!enabled) return;

        String displayName = plugin.getNickManager().getDisplayName(player);
        Component tabName = Component.text(displayName).color(NamedTextColor.WHITE);
        player.playerListName(tabName);
    }

    public void updateAllPlayersTab() {
        if (!enabled) return;

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            updatePlayerTabName(onlinePlayer);
        }
    }

    public void restoreAllPlayersTab() {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            String originalName = plugin.getNickManager().getOriginalName(onlinePlayer);
            Component originalTabName = Component.text(originalName).color(NamedTextColor.WHITE);
            onlinePlayer.playerListName(originalTabName);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}