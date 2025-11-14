package cn.aq3.anticheat.ml;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.player.PlayerData;
import java.util.*;

/**
 * 作弊模式检测器 - 使用简单的机器学习方法检测复杂作弊
 * Cheat pattern detector - Uses simple machine learning methods to detect complex cheating
 */
public class CheatPatternDetector {
    // 存储玩家行为模式
    // Store player behavior patterns
    private final Map<UUID, List<PlayerBehavior>> playerBehaviors = new HashMap<>();
    
    // 正常行为模式数据库
    // Normal behavior pattern database
    private final List<PlayerBehavior> normalPatterns = new ArrayList<>();
    
    // 异常阈值
    // Anomaly threshold
    private static final double ANOMALY_THRESHOLD = 0.8;
    
    /**
     * 分析玩家行为
     * Analyze player behavior
     */
    public boolean analyzePlayerBehavior(PlayerData playerData) {
        // 检查玩家行为历史是否足够
        // Check if player behavior history is sufficient
        List<PlayerBehavior> behaviors = playerBehaviors.get(playerData.getUuid());
        if (behaviors == null || behaviors.size() < 5) {
            // 行为历史不足，无法准确判断
            // Insufficient behavior history to make accurate judgment
            return false;
        }
        
        PlayerBehavior behavior = extractBehaviorFeatures(playerData);
        
        // 更新玩家行为历史
        // Update player behavior history
        updatePlayerBehaviorHistory(playerData.getUuid(), behavior);
        
        // 检查是否异常
        // Check if anomalous
        return isAnomalousBehavior(behavior, playerData);
    }
    
    /**
     * 提取行为特征
     * Extract behavior features
     */
    private PlayerBehavior extractBehaviorFeatures(PlayerData playerData) {
        PlayerBehavior behavior = new PlayerBehavior();
        
        // 提取移动特征
        // Extract movement features
        behavior.setMovementSpeed(calculateMovementSpeed(playerData));
        behavior.setMovementConsistency(calculateMovementConsistency(playerData));
        
        // 提取视角特征
        // Extract look features
        behavior.setLookSpeed(calculateLookSpeed(playerData));
        behavior.setLookConsistency(calculateLookConsistency(playerData));
        
        // 提取交互特征
        // Extract interaction features
        behavior.setInteractionFrequency(calculateInteractionFrequency(playerData));
        
        return behavior;
    }
    
    /**
     * 计算移动速度
     * Calculate movement speed
     */
    private double calculateMovementSpeed(PlayerData playerData) {
        double deltaX = playerData.getX() - playerData.getLastX();
        double deltaY = playerData.getY() - playerData.getLastY();
        double deltaZ = playerData.getZ() - playerData.getLastZ();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    /**
     * 计算移动一致性
     * Calculate movement consistency
     */
    private double calculateMovementConsistency(PlayerData playerData) {
        // 简化实现 - 实际中需要更复杂的算法
        // Simplified implementation - more complex algorithms needed in practice
        return 1.0;
    }
    
    /**
     * 计算视角速度
     * Calculate look speed
     */
    private double calculateLookSpeed(PlayerData playerData) {
        double deltaYaw = Math.abs(playerData.getYaw() - playerData.getLastYaw());
        double deltaPitch = Math.abs(playerData.getPitch() - playerData.getLastPitch());
        return Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
    }
    
    /**
     * 计算视角一致性
     * Calculate look consistency
     */
    private double calculateLookConsistency(PlayerData playerData) {
        // 简化实现 - 实际中需要更复杂的算法
        // Simplified implementation - more complex algorithms needed in practice
        return 1.0;
    }
    
    /**
     * 计算交互频率
     * Calculate interaction frequency
     */
    private double calculateInteractionFrequency(PlayerData playerData) {
        // 简化实现 - 实际中需要跟踪玩家的交互行为
        // Simplified implementation - need to track player interactions in practice
        return 1.0;
    }
    
    /**
     * 更新玩家行为历史
     * Update player behavior history
     */
    private void updatePlayerBehaviorHistory(UUID playerUUID, PlayerBehavior behavior) {
        playerBehaviors.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(behavior);
        
        // 限制历史记录大小
        // Limit history size
        List<PlayerBehavior> behaviors = playerBehaviors.get(playerUUID);
        if (behaviors.size() > 100) {
            behaviors.remove(0);
        }
    }
    
    /**
     * 检查是否为异常行为
     * Check if behavior is anomalous
     */
    private boolean isAnomalousBehavior(PlayerBehavior behavior, PlayerData playerData) {
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        
        // 简化实现 - 实际中需要使用机器学习模型
        // Simplified implementation - need to use machine learning models in practice
        
        // 基于简单规则检测异常，同时考虑阈值调整
        // Detect anomalies based on simple rules, considering threshold adjustment
        if (behavior.getMovementSpeed() > 1.0 * thresholdMultiplier && behavior.getLookSpeed() > 100.0 * thresholdMultiplier) {
            // 高速移动伴随快速视角变化可能是KillAura
            // High-speed movement with rapid look changes may be KillAura
            return true;
        }
        
        if (behavior.getMovementConsistency() > 0.95 * thresholdMultiplier && behavior.getLookConsistency() > 0.95 * thresholdMultiplier) {
            // 过于一致的移动和视角变化可能是机器人
            // Too consistent movement and look changes may be a bot
            return true;
        }
        
        return false;
    }
    
    /**
     * 添加正常行为模式
     * Add normal behavior pattern
     */
    public void addNormalPattern(PlayerBehavior behavior) {
        normalPatterns.add(behavior);
    }
}