package cn.aq3.anticheat.violations;

import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 违规管理器
 * Violation manager
 */
public class ViolationManager {
    // 存储每个玩家每种检查的违规次数
    // Store violation counts for each player and each check
    private final Map<UUID, Map<String, Integer>> playerViolations = new ConcurrentHashMap<>();
    
    /**
     * 记录违规
     * Record violation
     */
    public void addViolation(UUID playerUUID, Check check) {
        playerViolations
            .computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
            .merge(check.getName(), 1, Integer::sum);
    }
    
    /**
     * 获取玩家特定检查的违规次数
     * Get violation count for a specific check for a player
     */
    public int getViolationCount(UUID playerUUID, String checkName) {
        return playerViolations
            .getOrDefault(playerUUID, new ConcurrentHashMap<>())
            .getOrDefault(checkName, 0);
    }
    
    /**
     * 获取玩家总违规次数
     * Get total violation count for a player
     */
    public int getTotalViolations(UUID playerUUID) {
        return playerViolations
            .getOrDefault(playerUUID, new ConcurrentHashMap<>())
            .values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
    
    /**
     * 重置玩家违规记录
     * Reset player violation records
     */
    public void resetViolations(UUID playerUUID) {
        playerViolations.remove(playerUUID);
    }
}