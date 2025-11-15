package cn.aq3.anticheat.checks.inventory;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 背包移动检查 - 检测在移动时进行背包操作
 * Inventory Move Check - Detect inventory operations while moving
 */
public class InventoryMoveCheck extends Check {
    // 移动时允许的最大速度
    // Maximum speed allowed when moving
    private static final double MAX_MOVE_SPEED_WHILE_INVENTORY = 0.1;
    
    public InventoryMoveCheck() {
        super("InventoryMove", "检测移动时操作背包作弊", true, 10);
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
        
        // 检查玩家是否在操作背包
        // Check if player is operating inventory
        // 在实际实现中，需要从Bukkit事件中获取这个信息
        // In actual implementation, need to get this information from Bukkit events
        boolean isInInventory = false; // 简化实现 / Simplified implementation
        
        if (!isInInventory) {
            return false;
        }
        
        // 计算玩家移动速度
        // Calculate player movement speed
        double horizontalDistance = playerData.getHorizontalDistance();
        double verticalDistance = Math.abs(playerData.getVerticalDistance());
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        double maxMoveSpeed = MAX_MOVE_SPEED_WHILE_INVENTORY * thresholdMultiplier;
        
        // 检查是否在操作背包时移动过快
        // Check if moving too fast while operating inventory
        return horizontalDistance > maxMoveSpeed || verticalDistance > maxMoveSpeed;
    }
}