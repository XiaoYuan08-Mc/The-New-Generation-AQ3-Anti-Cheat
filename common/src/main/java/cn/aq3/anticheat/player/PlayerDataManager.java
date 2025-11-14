package cn.aq3.anticheat.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家数据管理器
 * Player data manager
 */
public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    
    /**
     * 添加玩家数据
     * Add player data
     */
    public void addPlayerData(PlayerData playerData) {
        playerDataMap.put(playerData.getUuid(), playerData);
    }
    
    /**
     * 移除玩家数据
     * Remove player data
     */
    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }
    
    /**
     * 获取玩家数据
     * Get player data
     */
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }
    
    /**
     * 检查玩家数据是否存在
     * Check if player data exists
     */
    public boolean hasPlayerData(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }
    
    /**
     * 获取所有玩家数据
     * Get all player data
     */
    public Map<UUID, PlayerData> getAllPlayerData() {
        return new ConcurrentHashMap<>(playerDataMap);
    }
}