package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 水上行走检查（Jesus）
 * Water walking check (Jesus)
 */
public class JesusCheck extends Check {
    
    public JesusCheck() {
        super("Jesus", "检测水上行走作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 根据灵敏度调整检测阈值
        // Adjust detection threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        
        // 检查玩家是否刚刚加入游戏，如果是则跳过检查
        // Check if player just joined the game, skip check if so
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) { // 5秒内加入游戏的玩家不检查 / Don't check players who joined in the last 5 seconds
            return false;
        }
        
        // 检查玩家是否在特殊状态（如蹲下）
        // Check if player is in special state (e.g. sneaking)
        if (playerData.isSneaking() || playerData.wasSneaking()) {
            // 蹲下时可能在水面上，不判定为作弊
            // When sneaking, may be on water surface, don't判定 of cheat
            return false;
        }
        
        // 检查是否刚刚从地面跳跃，这可能会导致误判
        // Check if just jumped from ground, which may cause false positives
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            return false;
        }
        
        // 检查玩家是否在水上行走
        // Check if player is walking on water
        boolean isOnWaterSurface = isOnWaterSurface(playerData);
        boolean isMovingUpwards = (playerData.getY() - playerData.getLastY()) > 0.05 * thresholdMultiplier; // 使用阈值调整
        
        // 如果玩家在水面上且向上移动，则可能是作弊
        // If player is on water surface and moving upward, may be cheating
        return isOnWaterSurface && isMovingUpwards;
    }
    
    /**
     * 检查玩家是否在水面上
     * Check if player is on water surface
     */
    private boolean isOnWaterSurface(PlayerData playerData) {
        // 更准确的实现需要检查玩家下方的方块
        // More accurate implementation would check blocks below player
        // 这里简化实现，实际项目中需要检查玩家站立的方块是否为水
        // Simplified implementation, in a real project you'd check if the block the player is standing on is water
        double yDecimal = playerData.getY() - Math.floor(playerData.getY());
        return yDecimal > 0.5 && yDecimal < 0.6; // 简单示例 / Simple example
    }
}