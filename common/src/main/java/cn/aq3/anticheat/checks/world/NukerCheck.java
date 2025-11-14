package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 范围挖掘检查 - 检测玩家异常的方块破坏模式
 * Nuker Check - Detect abnormal block breaking patterns
 */
public class NukerCheck extends Check {
    // 存储玩家方块破坏记录
    // Store player block breaking records
    private final Map<UUID, Long> lastBreakTimes = new HashMap<>();
    private final Map<UUID, Integer> breakCounts = new HashMap<>();
    
    // 时间窗口（毫秒）
    // Time window (milliseconds)
    private static final long TIME_WINDOW = 1000; // 1秒 / 1 second
    
    // 最大允许的方块破坏数量（每秒）
    // Maximum allowed block breaks (per second)
    private static final int MAX_BREAKS_PER_SECOND = 25;
    
    public NukerCheck() {
        super("Nuker", "检测范围挖掘作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 检查玩家是否刚刚加入游戏，如果是则跳过检查
        // Check if player just joined the game, skip check if so
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 10000) { // 10秒内加入游戏的玩家不检查 / Don't check players who joined in the last 10 seconds
            return false;
        }
        
        UUID playerUUID = playerData.getUuid();
        
        // 更新方块破坏记录
        // Update block break record
        long currentTime = System.currentTimeMillis();
        lastBreakTimes.put(playerUUID, currentTime);
        breakCounts.put(playerUUID, breakCounts.getOrDefault(playerUUID, 0) + 1);
        
        // 清理过期记录
        // Clean up expired records
        cleanupExpiredRecords(currentTime);
        
        // 计算破坏速率
        // Calculate break rate
        int breakCount = breakCounts.getOrDefault(playerUUID, 0);
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        int maxBreaks = (int) (MAX_BREAKS_PER_SECOND * thresholdMultiplier);
        
        // 检查是否超出正常范围
        // Check if out of normal range
        return breakCount > maxBreaks;
    }
    
    /**
     * 清理过期记录
     * Clean up expired records
     */
    private void cleanupExpiredRecords(long currentTime) {
        lastBreakTimes.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > TIME_WINDOW);
        
        // 重置计数器
        // Reset counters
        for (Map.Entry<UUID, Long> entry : lastBreakTimes.entrySet()) {
            if ((currentTime - entry.getValue()) <= TIME_WINDOW) {
                // 仍在时间窗口内，保留计数
                // Still within time window, keep count
            } else {
                // 超出时间窗口，重置计数
                // Exceeded time window, reset count
                breakCounts.put(entry.getKey(), 0);
            }
        }
    }
    
    /**
     * 记录方块破坏事件
     * Record block break event
     */
    public void recordBlockBreak(PlayerData playerData) {
        // 在实际实现中，这个方法将通过ProtocolLib数据包监听器调用
        // In actual implementation, this method will be called by ProtocolLib packet listener
        performCheck(playerData);
    }
}