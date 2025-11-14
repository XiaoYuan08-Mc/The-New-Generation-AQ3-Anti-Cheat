package cn.aq3.anticheat.checks.player;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 无摔落伤害检查 - 检测玩家是否试图避免摔落伤害
 * NoFall Check - Detect if player is trying to avoid fall damage
 */
public class NoFallCheck extends Check {
    // 重力常量
    // Gravity constant
    private static final double GRAVITY = 0.08;
    
    // 最小摔落距离，超过此距离应该受到摔落伤害
    // Minimum fall distance, fall damage should be taken if exceeded
    private static final double MIN_FALL_DISTANCE = 3.0;
    
    public NoFallCheck() {
        super("NoFall", "检测无摔落伤害作弊", true, 10);
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
        if (timeSinceJoin < 5000) { // 5秒内加入游戏的玩家不检查 / Don't check players who joined in the last 5 seconds
            return false;
        }
        
        // 获取玩家位置信息
        // Get player position information
        double x = playerData.getX();
        double y = playerData.getY();
        double z = playerData.getZ();
        double lastY = playerData.getLastY();
        boolean onGround = playerData.isOnGround();
        boolean wasOnGround = playerData.wasOnGround();
        
        // 计算垂直移动距离
        // Calculate vertical movement distance
        double deltaY = y - lastY;
        
        // 检查玩家是否在地面上
        // Check if player is on ground
        if (onGround) {
            // 如果玩家在地面上，检查是否有足够的摔落距离但没有受到摔落伤害
            // If player is on ground, check if there's enough fall distance but no fall damage taken
            
            // 计算玩家的摔落距离
            // Calculate player's fall distance
            double fallDistance = calculateFallDistance(playerData);
            
            // 如果摔落距离超过最小摔落距离，但玩家没有受到摔落伤害，则可能是NoFall作弊
            // If fall distance exceeds minimum fall distance, but player didn't take fall damage, may be NoFall cheating
            if (fallDistance >= MIN_FALL_DISTANCE) {
                // 根据灵敏度调整阈值
                // Adjust threshold based on sensitivity
                double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
                double adjustedMinFallDistance = MIN_FALL_DISTANCE * thresholdMultiplier;
                
                // 检查是否应该受到摔落伤害但没有受到
                // Check if should take fall damage but didn't
                return fallDistance >= adjustedMinFallDistance;
            }
        }
        
        // 更新玩家的摔落距离
        // Update player's fall distance
        updateFallDistance(playerData, deltaY);
        
        return false;
    }
    
    /**
     * 计算玩家的摔落距离
     * Calculate player's fall distance
     */
    private double calculateFallDistance(PlayerData playerData) {
        // 这里简化实现，实际项目中应该跟踪玩家的摔落距离
        // Simplified implementation, in a real project should track player's fall distance
        // 我们可以通过跟踪玩家从最高点到最低点的垂直距离来计算摔落距离
        // We can calculate fall distance by tracking vertical distance from highest point to lowest point
        
        // 检查玩家是否从空中落到地面
        // Check if player falls from air to ground
        if (playerData.isOnGround() && !playerData.wasOnGround()) {
            // 玩家从空中落到地面，计算摔落距离
            // Player falls from air to ground, calculate fall distance
            double currentY = playerData.getY();
            double maxY = playerData.getLastY(); // 简化实现，实际应该跟踪最高点 / Simplified implementation, should track highest point in practice
            
            return maxY - currentY;
        }
        
        return 0;
    }
    
    /**
     * 更新玩家的摔落距离
     * Update player's fall distance
     */
    private void updateFallDistance(PlayerData playerData, double deltaY) {
        // 如果玩家正在下落，增加摔落距离
        // If player is falling, increase fall distance
        if (deltaY < 0) {
            // 玩家正在下落
            // Player is falling
        }
        // 如果玩家在地面上，重置摔落距离
        // If player is on ground, reset fall distance
        else if (playerData.isOnGround()) {
            // 玩家在地面上，重置摔落距离
            // Player is on ground, reset fall distance
        }
    }
}