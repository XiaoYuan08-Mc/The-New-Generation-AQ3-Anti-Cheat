package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 相位检查 - 检测玩家穿墙或穿方块作弊
 * Phase Check - Detect players clipping through walls or blocks
 */
public class PhaseCheck extends Check {
    // 最小移动距离阈值
    // Minimum movement distance threshold
    private static final double MIN_MOVEMENT = 0.01;
    
    public PhaseCheck() {
        super("Phase", "检测相位穿墙作弊", true, 10);
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
        double lastX = playerData.getLastX();
        double lastY = playerData.getLastY();
        double lastZ = playerData.getLastZ();
        
        // 计算位置变化
        // Calculate position changes
        double deltaX = Math.abs(x - lastX);
        double deltaY = Math.abs(y - lastY);
        double deltaZ = Math.abs(z - lastZ);
        
        // 如果没有明显移动，则跳过检查
        // Skip check if there's no significant movement
        if (deltaX < MIN_MOVEMENT && deltaY < MIN_MOVEMENT && deltaZ < MIN_MOVEMENT) {
            return false;
        }
        
        // 检查是否穿过固体方块
        // Check if passing through solid blocks
        boolean isPhasing = checkBlockPhasing(playerData);
        
        // 根据灵敏度调整检测严格程度
        // Adjust detection strictness based on sensitivity
        if (sensitivity <= 1 && isPhasing) {
            // 灵敏度最低时，只在明显穿墙时触发
            // At lowest sensitivity, only trigger on obvious wall phasing
            return checkObviousPhasing(playerData);
        }
        
        return isPhasing;
    }
    
    /**
     * 检查是否穿过方块
     * Check if passing through blocks
     */
    private boolean checkBlockPhasing(PlayerData playerData) {
        // 这里简化实现，实际应用中需要检查玩家包围盒与方块的碰撞
        // Simplified implementation, in practice need to check collision between player bounding box and blocks
        
        // 检查玩家当前位置是否在固体方块内部
        // Check if player's current position is inside a solid block
        // 在完整实现中，这需要访问世界数据来检查方块类型
        // In full implementation, this needs to access world data to check block types
        
        // 示例逻辑 - 实际实现需要访问Bukkit API
        // Sample logic - actual implementation needs to access Bukkit API
        return false;
    }
    
    /**
     * 检查明显的穿墙行为
     * Check for obvious phasing behavior
     */
    private boolean checkObviousPhasing(PlayerData playerData) {
        // 检查大距离瞬移但未经过中间点的情况
        // Check for large distance teleportation without passing through intermediate points
        double deltaX = Math.abs(playerData.getX() - playerData.getLastX());
        double deltaY = Math.abs(playerData.getY() - playerData.getLastY());
        double deltaZ = Math.abs(playerData.getZ() - playerData.getLastZ());
        
        // 如果单轴移动超过一定距离且未经过合理的中间过程，则可能是穿墙
        // If single-axis movement exceeds a certain distance without reasonable intermediate process, may be wall phasing
        return deltaX > 2.0 || deltaY > 2.0 || deltaZ > 2.0;
    }
}