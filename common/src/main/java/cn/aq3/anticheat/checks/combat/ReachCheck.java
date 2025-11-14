package cn.aq3.anticheat.checks.combat;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 攻击距离检查
 * Reach check
 */
public class ReachCheck extends Check {
    private static final double MAX_REACH = 3.0; // 正常攻击距离 / Normal reach distance
    
    public ReachCheck() {
        super("Reach", "检测超距离攻击作弊", true, 10);
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
        
        // 在实际实现中，我们需要获取玩家与目标实体的距离
        // In actual implementation, we need to get the distance between player and target entity
        // 这里只是一个示例实现
        // This is just a sample implementation
        
        double reachDistance = calculateReachDistance(playerData);
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.1; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        
        return reachDistance > MAX_REACH * thresholdMultiplier;
    }
    
    /**
     * 计算攻击距离（示例实现）
     * Calculate reach distance (sample implementation)
     */
    private double calculateReachDistance(PlayerData playerData) {
        // 简化的距离计算，实际应根据目标实体位置计算
        // Simplified distance calculation, should actually calculate based on target entity position
        return Math.sqrt(
            Math.pow(playerData.getX() - playerData.getLastX(), 2) +
            Math.pow(playerData.getY() - playerData.getLastY(), 2) +
            Math.pow(playerData.getZ() - playerData.getLastZ(), 2)
        );
    }
}