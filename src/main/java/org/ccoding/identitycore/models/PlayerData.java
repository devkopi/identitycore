package org.ccoding.identitycore.models;

import java.util.UUID;

public class PlayerData {

    private final UUID playerId;
    private final String originalName;
    private String currentNick;
    private String colorCode;
    private String formatCode;
    private String advancedNick;
    private long lastUpdate;

    public PlayerData(UUID playerId, String originalName) {
        this.playerId = playerId;
        this.originalName = originalName;
        this.currentNick = originalName;
        this.lastUpdate = System.currentTimeMillis();
    }

    // Getters y setters
    public UUID getPlayerId() { return  playerId; }
    public String getOriginalName() { return originalName; }
    public String getCurrentNick() { return currentNick; }
    public void setCurrentNick(String currentNick) {
        this.currentNick = currentNick;
        this.lastUpdate = System.currentTimeMillis();
    }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    public String getFormatCode() { return formatCode; }
    public void setFormatCode(String formatCode) { this.formatCode = formatCode; }
    public String getAdvancedNick() { return advancedNick; }
    public void setAdvancedNick(String advancedNick) { this.advancedNick = advancedNick; }
    public long getLastUpdate() { return lastUpdate; }
}
