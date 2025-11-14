package cn.aq3.anticheat.checks.combat;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 暴击检查 - 检测非法暴击作弊
 * Criticals Check - Detect illegal critical hit cheats
 */
public class CriticalsCheck extends Check {
    // 玩家必须在空中的最小时间（毫秒）才能造成合法暴击
    // Minimum time (milliseconds) player must be in air to make legal critical hit
    private static final long MIN_AIR_TIME_FOR_CRITICAL = 100;
    
    public CriticalsCheck() {
        super("Criticals", "检测非法暴击作弊", true, 10);
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
        
        // 检查玩家是否刚刚进行了攻击
        // Check if player just performed an attack
        long timeSinceAttack = System.currentTimeMillis() - playerData.getLastAttackTime();
        if (timeSinceAttack > 1000) { // 如果1秒内没有攻击，则跳过检查 / Skip check if no attack within 1 second
            return false;
        }
        
        // 检查暴击条件
        // Check critical hit conditions
        boolean isOnGround = playerData.isOnGround();
        boolean wasOnGround = playerData.wasOnGround();
        double fallDistance = playerData.getFallDistance();
        
        // 检查是否在地面上造成了暴击
        // Check if critical hit was made on ground
        boolean criticalOnGround = checkCriticalOnGround(isOnGround, wasOnGround);
        
        // 检查是否在不应该造成暴击的情况下造成了暴击
        // Check if critical hit was made when it shouldn't be possible
        boolean invalidCritical = checkInvalidCritical(playerData);
        
        // 根据灵敏度调整检测严格程度
        // Adjust detection strictness based on sensitivity
        if (sensitivity <= 2) {
            // 在较低灵敏度下，只检测明显的违规行为
            // At lower sensitivity, only detect obvious violations
            return criticalOnGround;
        }
        
        return criticalOnGround || invalidCritical;
    }
    
    /**
     * 检查是否在地面上造成了暴击
     * Check if critical hit was made on ground
     */
    private boolean checkCriticalOnGround(boolean isOnGround, boolean wasOnGround) {
        // 如果玩家当前在地面上或刚刚在地面上，则不应该造成暴击
        // If player is currently on ground or was just on ground, should not cause critical hit
        return isOnGround || wasOnGround;
    }
    
    /**
     * 检查无效暴击
     * Check invalid critical
     */
    private boolean checkInvalidCritical(PlayerData playerData) {
        // 检查玩家是否在合理的时间内在空中
        // Check if player has been in air for a reasonable time
        long timeInAir = System.currentTimeMillis() - playerData.getLastOnGroundTime();
        
        // 如果玩家在空中时间太短，则可能是非法暴击
        // If player has been in air for too short a time, may be illegal critical
        return timeInAir < MIN_AIR_TIME_FOR_CRITICAL;
    }
}