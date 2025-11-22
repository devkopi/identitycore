package org.ccoding.identitycore.managers;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ccoding.identitycore.IdentityCore;
import org.ccoding.identitycore.models.PlayerData;
import org.ccoding.identitycore.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class NickManager {

    private final IdentityCore plugin;
    private final HashMap<UUID, PlayerData> playerDataMap;
    private File dataFile;
    private FileConfiguration dataConfig;

    public NickManager(IdentityCore plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
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
                String playerColor = dataConfig.getString("players." + uuidString + ".color");
                String playerFormat = dataConfig.getString("players." + uuidString + ".format");

                if (originalName != null) {
                    PlayerData data = new PlayerData(uuid, originalName);
                    if (currentNick != null) data.setCurrentNick(currentNick);
                    if (playerColor != null) data.setColorCode(playerColor);
                    if (playerFormat != null) data.setFormatCode(playerFormat);
                    playerDataMap.put(uuid, data);
                }
            }
        }
    }

    public void setNick(Player player, String nick) {
        UUID uuid = player.getUniqueId();
        PlayerData data = getOrCreatePlayerData(player);
        data.setCurrentNick(nick);
        savePlayerData(uuid);
    }

    public void restoreOriginalName(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            data.setCurrentNick(data.getOriginalName());
            data.setColorCode(null);
            data.setFormatCode(null);
            data.setAdvancedNick(null);
            savePlayerData(uuid);
        }
    }

    public String getDisplayName(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getCurrentNick() : player.getName();
    }

    public String getOriginalName(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getOriginalName() : player.getName();
    }

    public String getCurrentNick(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getCurrentNick() : null;
    }

    public boolean hasNick(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null && !data.getCurrentNick().equals(data.getOriginalName());
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String uuidString = uuid.toString();

        if (dataConfig.contains("players." + uuidString)) {
            String originalName = dataConfig.getString("players." + uuidString + ".originalName");
            String currentNick = dataConfig.getString("players." + uuidString + ".currentNick");
            String playerColor = dataConfig.getString("players." + uuidString + ".color");
            String playerFormat = dataConfig.getString("players." + uuidString + ".format");

            if (originalName != null) {
                PlayerData data = new PlayerData(uuid, originalName);
                if (currentNick != null) data.setCurrentNick(currentNick);
                if (playerColor != null) data.setColorCode(playerColor);
                if (playerFormat != null) data.setFormatCode(playerFormat);
                playerDataMap.put(uuid, data);
            }
        }
    }

    // Aplicar configuración de color al nick
    public void applyColorConfig(Player player, String color, String format) {
        PlayerData data = getOrCreatePlayerData(player);
        if (color != null) data.setColorCode(color);
        if (format != null) data.setFormatCode(format);
        savePlayerData(player.getUniqueId());
    }


    // Obtener nick con colores aplicados (funciona para ambos sistemas)
    public String getColoredDisplayName(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) return player.getName();

        String baseNick = data.getCurrentNick();

        // Si el nick tiene códigos & (editor avanzado), procesarlos
        if (baseNick != null && baseNick.contains("&")) {
            return MessageUtils.formatColors(baseNick);
        }

        // Si no, aplicar color único (sistema normal)
        String color = data.getColorCode();
        String format = data.getFormatCode();

        if (color != null || format != null) {
            String coloredNick = (color != null ? color : "") + (format != null ? format : "") + baseNick;
            return MessageUtils.formatColors(coloredNick);
        }

        // Si no hay nada, devolver base sin formato
        return baseNick;
    }

    // Obtener la configuración de color del jugador
    public String getPlayerColor(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getColorCode() : null;
    }

    // Obtener la configuración de formato del jugador
    public String getPlayerFormat(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getFormatCode() : null;
    }

    /**
     * Guarda un nick con códigos de color avanzados
     */
    public void setAdvancedNick(Player player, String coloredNick) {
        PlayerData data = getOrCreatePlayerData(player);
        data.setAdvancedNick(coloredNick);
        data.setCurrentNick(coloredNick);
        savePlayerData(player.getUniqueId());
    }

    private PlayerData getOrCreatePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) {
            data = new PlayerData(uuid, player.getName());
            playerDataMap.put(uuid, data);
        }
        return data;
    }

    public HashMap<UUID, String> getPlayerColors() {
        HashMap<UUID, String> colors = new HashMap<>();
        for (PlayerData data : playerDataMap.values()) {
            if (data.getColorCode() != null) {
                colors.put(data.getPlayerId(), data.getColorCode());
            }
        }
        return colors;
    }

    public HashMap<UUID, String> getPlayerFormats() {
       HashMap<UUID, String> formats = new HashMap<>();
       for (PlayerData data : playerDataMap.values()) {
           if (data.getFormatCode() != null) {
               formats.put(data.getPlayerId(), data.getFormatCode());
           }
       }
       return formats;
    }

    private void savePlayerData(UUID uuid) {
        String uuidString = uuid.toString();
        PlayerData data = playerDataMap.get(uuid);

        if (data != null) {
            dataConfig.set("players." + uuidString + ".originalName", data.getOriginalName());
            dataConfig.set("players." + uuidString + ".currentNick", data.getCurrentNick());
            dataConfig.set("players." + uuidString + ".color", data.getColorCode());
            dataConfig.set("players." + uuidString + ".format", data.getFormatCode());
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