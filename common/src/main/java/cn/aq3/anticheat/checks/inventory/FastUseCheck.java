package cn.aq3.anticheat.checks.inventory;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 物品使用速度检查
 * Fast use check
 */
public class FastUseCheck extends Check {
    private static final long MIN_USE_TIME = 1000; // 最短使用时间（毫秒） / Minimum use time (milliseconds)
    
    public FastUseCheck() {
        super("FastUse", "检测快速使用物品作弊", true, 10);
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
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        long minUseTime = (long) (MIN_USE_TIME / thresholdMultiplier);
        
        // 检查物品使用间隔是否过短
        // Check if item use interval is too short
        long currentTime = System.currentTimeMillis();
        long lastUseTime = playerData.getLastUseTime();
        
        if (lastUseTime > 0 && (currentTime - lastUseTime) < minUseTime) {
            return true;
        }
        
        // 更新最后使用时间
        // Update last use time
        playerData.setLastUseTime(currentTime);
        return false;
    }
}