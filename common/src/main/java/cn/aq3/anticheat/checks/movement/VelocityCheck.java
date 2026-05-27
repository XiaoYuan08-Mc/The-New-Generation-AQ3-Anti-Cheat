package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 速度检查 - 检测玩家速度异常和防击退作弊
 * Velocity Check - Detect abnormal player velocity and anti-knockback cheats
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
    
    // 最小速度减少百分比（正常情况下击退应该减少玩家一些速度）
    // Minimum velocity reduction percentage (normally knockback should reduce player's velocity)
    private static final double MIN_VELOCITY_REDUCTION = 0.15;
    
    public VelocityCheck() {
        super("Velocity", "检测异常速度和防击退作弊", true, 10);
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
        
        // 检查是否最近受到了击退
        // Check if recently took knockback
        boolean checkAntiKB = false;
        if (playerData.isVelocityModified() && 
            System.currentTimeMillis() - playerData.getLastVelocityTime() < 1000) {
            checkAntiKB = true;
        }
        
        if (checkAntiKB) {
            // 检查防击退
            // Check anti-knockback
            boolean antiKBFlag = checkAntiKnockback(playerData);
            if (antiKBFlag) {
                return true;
            }
        }
        
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
     * 检查防击退作弊
     * Check for anti-knockback cheats
     */
    private boolean checkAntiKnockback(PlayerData playerData) {
        // 获取预期的速度变化
        // Get expected velocity changes
        double expectedVelocityX = playerData.getVelocityTakenX();
        double expectedVelocityY = playerData.getVelocityTakenY();
        double expectedVelocityZ = playerData.getVelocityTakenZ();
        
        // 获取实际的速度变化
        // Get actual velocity changes
        double actualVelocityX = playerData.getVelocityX();
        double actualVelocityY = playerData.getVelocityY();
        double actualVelocityZ = playerData.getVelocityZ();
        
        // 计算速度减少的百分比
        // Calculate velocity reduction percentage
        double expectedMagnitude = Math.sqrt(
            expectedVelocityX * expectedVelocityX + 
            expectedVelocityY * expectedVelocityY + 
            expectedVelocityZ * expectedVelocityZ
        );
        
        double actualMagnitude = Math.sqrt(
            actualVelocityX * actualVelocityX + 
            actualVelocityY * actualVelocityY + 
            actualVelocityZ * actualVelocityZ
        );
        
        // 如果预期的速度改变很小，则跳过检查
        // If expected velocity change is too small, skip check
        if (expectedMagnitude < 0.1) {
            return false;
        }
        
        // 检查实际速度是否远小于预期速度
        // Check if actual velocity is much smaller than expected
        double reduction = 1.0 - (actualMagnitude / expectedMagnitude);
        if (reduction < MIN_VELOCITY_REDUCTION) {
            // 玩家没有受到足够的击退，可能使用了防击退作弊
            // Player didn't take enough knockback, may be using anti-knockback cheat
            return true;
        }
        
        return false;
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
