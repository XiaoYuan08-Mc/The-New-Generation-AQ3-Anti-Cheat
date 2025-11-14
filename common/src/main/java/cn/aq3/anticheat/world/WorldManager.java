package cn.aq3.anticheat.world;

import cn.aq3.anticheat.player.PlayerData;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界管理器 - 管理所有玩家的世界副本
 * World Manager - Manages world replicas for all players
 */
public class WorldManager {
    private final Map<UUID, PlayerWorld> playerWorlds = new ConcurrentHashMap<>();
    
    /**
     * 为玩家创建世界副本
     * Create world replica for player
     */
    public void createWorldForPlayer(UUID playerUUID) {
        playerWorlds.put(playerUUID, new PlayerWorld());
    }
    
    /**
     * 移除玩家的世界副本
     * Remove player's world replica
     */
    public void removeWorldForPlayer(UUID playerUUID) {
        playerWorlds.remove(playerUUID);
    }
    
    /**
     * 获取玩家的世界副本
     * Get player's world replica
     */
    public PlayerWorld getWorldForPlayer(UUID playerUUID) {
        return playerWorlds.get(playerUUID);
    }
    
    /**
     * 更新玩家世界中的方块
     * Update block in player's world
     */
    public void updateBlock(UUID playerUUID, WorldBlock block) {
        PlayerWorld world = playerWorlds.get(playerUUID);
        if (world != null) {
            world.addBlock(block);
        }
    }
    
    /**
     * 移除玩家世界中的方块
     * Remove block from player's world
     */
    public void removeBlock(UUID playerUUID, int x, int y, int z) {
        PlayerWorld world = playerWorlds.get(playerUUID);
        if (world != null) {
            world.removeBlock(x, y, z);
        }
    }
}