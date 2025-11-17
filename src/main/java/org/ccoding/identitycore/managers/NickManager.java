package org.ccoding.identitycore.managers;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ccoding.identitycore.IdentityCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class NickManager {

    private final IdentityCore plugin;
    private final HashMap<UUID, String> originalNames;
    private final HashMap<UUID, String> currentNicks;
    private File dataFile;
    private FileConfiguration dataConfig;

    public NickManager(IdentityCore plugin) {
        this.plugin = plugin;
        this.originalNames = new HashMap<>();
        this.currentNicks = new HashMap<>();
        setupDataFile();
        loadAllData();
    }

    private void setupDataFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        dataFile = new File(plugin.getDataFolder(), "players.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void loadAllData() {
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                String originalName = dataConfig.getString("players." + uuidString + ".originalName");
                String currentNick = dataConfig.getString("players." + uuidString + ".currentNick");

                if (originalName != null) {
                    originalNames.put(uuid, originalName);
                }
                if (currentNick != null) {
                    currentNicks.put(uuid, currentNick);
                }
            }
        }
    }

    public void setNick(Player player, String nick) {
        UUID uuid = player.getUniqueId();

        if (!originalNames.containsKey(uuid)) {
            originalNames.put(uuid, player.getName());
        }

        currentNicks.put(uuid, nick);
        savePlayerData(uuid);
    }

    public void restoreOriginalName(Player player) {
        UUID uuid = player.getUniqueId();

        if (hasNick(player)) {
            currentNicks.remove(uuid);
            savePlayerData(uuid);
        }
    }

    public String getDisplayName(Player player) {
        UUID uuid = player.getUniqueId();
        return currentNicks.getOrDefault(uuid, player.getName());
    }

    public String getOriginalName(Player player) {
        UUID uuid = player.getUniqueId();
        return originalNames.getOrDefault(uuid, player.getName());
    }

    public String getCurrentNick(Player player) {
        UUID uuid = player.getUniqueId();
        return currentNicks.get(uuid);
    }

    public boolean hasNick(Player player) {
        return currentNicks.containsKey(player.getUniqueId());
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String uuidString = uuid.toString();

        if (dataConfig.contains("players." + uuidString)) {
            String originalName = dataConfig.getString("players." + uuidString + ".originalName");
            String currentNick = dataConfig.getString("players." + uuidString + ".currentNick");

            if (originalName != null) {
                originalNames.put(uuid, originalName);
            }
            if (currentNick != null) {
                currentNicks.put(uuid, currentNick);
            }
        }
    }

    private void savePlayerData(UUID uuid) {
        String uuidString = uuid.toString();

        if (originalNames.containsKey(uuid)) {
            dataConfig.set("players." + uuidString + ".originalName", originalNames.get(uuid));
        }

        if (currentNicks.containsKey(uuid)) {
            dataConfig.set("players." + uuidString + ".currentNick", currentNicks.get(uuid));
        } else {
            dataConfig.set("players." + uuidString + ".currentNick", null);
        }

        saveDataFile();
    }

    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar players.yml: " + e.getMessage());
        }
    }

    public void saveAllData() {
        saveDataFile();
    }
}