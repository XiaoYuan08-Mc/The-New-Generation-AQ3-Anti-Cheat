package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 速度检查 - 检测玩家速度异常
 * Velocity Check - Detect abnormal player velocity
 */
public class VelocityCheck extends Check {
    // 重力常量
    // Gravity constant
    private static final double GRAVITY = 0.08;
    
    // 最大水平速度阈值
    // Maximum horizontal velocity threshold
    private static final double MAX_HORIZONTAL_VELOCITY = 1.0;
    
    // 最大垂直速度阈值
    // Maximum vertical velocity threshold
    private static final double MAX_VERTICAL_VELOCITY = 1.0;
    
    public VelocityCheck() {
        super("Velocity", "检测异常速度作弊", true, 10);
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
        
        // 获取玩家速度信息
        // Get player velocity information
        double velocityX = playerData.getVelocityX();
        double velocityY = playerData.getVelocityY();
        double velocityZ = playerData.getVelocityZ();
        
        // 计算水平速度
        // Calculate horizontal velocity
        double horizontalVelocity = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        
        // 根据灵敏度调整阈值
        // Adjust thresholds based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        double maxHorizontalVelocity = MAX_HORIZONTAL_VELOCITY * thresholdMultiplier;
        double maxVerticalVelocity = MAX_VERTICAL_VELOCITY * thresholdMultiplier;
        
        // 检查速度是否超出正常范围
        // Check if velocity exceeds normal range
        boolean horizontalExceeded = horizontalVelocity > maxHorizontalVelocity;
        boolean verticalExceeded = Math.abs(velocityY) > maxVerticalVelocity;
        
        // 检查不自然的速度变化
        // Check for unnatural velocity changes
        boolean unnaturalChange = checkUnnaturalVelocityChange(playerData);
        
        return horizontalExceeded || verticalExceeded || unnaturalChange;
    }
    
    /**
     * 检查不自然的速度变化
     * Check for unnatural velocity changes
     */
    private boolean checkUnnaturalVelocityChange(PlayerData playerData) {
        // 检查速度是否突然大幅改变而没有合理的来源（如击退、药水效果等）
        // Check if velocity suddenly changes drastically without reasonable source (like knockback, potion effects, etc.)
        
        double currentVelocityX = playerData.getVelocityX();
        double currentVelocityY = playerData.getVelocityY();
        double currentVelocityZ = playerData.getVelocityZ();
        
        double lastVelocityX = playerData.getLastVelocityX();
        double lastVelocityY = playerData.getLastVelocityY();
        double lastVelocityZ = playerData.getLastVelocityZ();
        
        // 计算速度变化
        // Calculate velocity changes
        double deltaVelocityX = Math.abs(currentVelocityX - lastVelocityX);
        double deltaVelocityY = Math.abs(currentVelocityY - lastVelocityY);
        double deltaVelocityZ = Math.abs(currentVelocityZ - lastVelocityZ);
        
        // 如果速度变化过大且没有合理的解释，则可能是作弊
        // If velocity change is too large without reasonable explanation, may be cheating
        return deltaVelocityX > 0.5 || deltaVelocityY > 0.5 || deltaVelocityZ > 0.5;
    }
}