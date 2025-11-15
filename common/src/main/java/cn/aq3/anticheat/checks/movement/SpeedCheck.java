package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.physics.PhysicsEngine;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 速度检查
 * Speed check
 */
public class SpeedCheck extends Check {
    
    public SpeedCheck() {
        super("Speed", "检测速度作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        // 检查是否启用（动态读取配置）
        // Check if enabled (dynamically read configuration)
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.movement.speed.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        // 获取最大违规次数（动态读取配置）
        // Get maximum violations (dynamically read configuration)
        int maxViolations = AQ3API.getInstance().getConfigManager()
            .getInt("checks.movement.speed.max_violations", 10);
        
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 根据灵敏度调整检测阈值，移动检查应该更加宽松
        // Adjust detection threshold based on sensitivity, movement checks should be more lenient
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        
        // 检查玩家是否刚刚加入游戏，如果是则跳过检查
        // Check if player just joined the game, skip check if so
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) { // 5秒内加入游戏的玩家不检查 / Don't check players who joined in the last 5 seconds
            return false;
        }
        
        // 检查玩家是否在传送或重生状态
        // Check if player is teleporting or respawning
        if (isTeleporting(playerData)) {
            return false;
        }
        
        // 检查玩家是否在特殊状态（如创造模式飞行）
        // Check if player is in special state (e.g. creative mode flying)
        if (isInSpecialState(playerData)) {
            return false;
        }
        
        // 使用物理引擎计算预期位置
        // Use physics engine to calculate expected position
        double[] expectedPos = PhysicsEngine.calculateExpectedPosition(
            playerData,
            false, // isSprinting
            playerData.isSneaking(), // isSneaking
            false, // isInWater
            false, // isInLava
            false, // isInCobweb
            false, // isInHoneyBlock
            false, // isOnSoulSpeedBlock
            playerData.isOnGround() // isOnGround
        );
        
        double expectedX = expectedPos[0];
        double expectedY = expectedPos[1];
        double expectedZ = expectedPos[2];
        
        // 检查移动是否符合物理规律
        // Check if movement conforms to physics
        boolean isValid = PhysicsEngine.isValidMovementWithThreshold(
            playerData, expectedX, expectedY, expectedZ, thresholdMultiplier
        );
        
        // 检查是否在合理范围内移动
        // Check if moving within reasonable bounds
        boolean withinBounds = PhysicsEngine.isMovingWithinBoundsWithThreshold(playerData, thresholdMultiplier);
        
        // 如果移动不符合物理规律或超出范围，则可能作弊
        // If movement doesn't conform to physics or exceeds bounds, may be cheating
        return !isValid || !withinBounds;
    }
    
    /**
     * 检查玩家是否在传送或重生状态
     * Check if player is teleporting or respawning
     */
    private boolean isTeleporting(PlayerData playerData) {
        // 检查是否发生了大的位置跳跃
        // Check for large position jumps
        double deltaX = Math.abs(playerData.getX() - playerData.getLastX());
        double deltaY = Math.abs(playerData.getY() - playerData.getLastY());
        double deltaZ = Math.abs(playerData.getZ() - playerData.getLastZ());
        
        // 如果位置变化超过一定阈值，认为是传送
        // If position change exceeds a certain threshold, consider it a teleport
        return deltaX > 5.0 || deltaY > 5.0 || deltaZ > 5.0;
    }
    
    /**
     * 检查玩家是否在特殊状态
     * Check if player is in special state
     */
    private boolean isInSpecialState(PlayerData playerData) {
        // 检查是否在蹲下状态切换
        // Check if toggling sneak state
        if (playerData.isSneaking() != playerData.wasSneaking()) {
            return true;
        }
        
        // 检查是否刚刚跳跃
        // Check if just jumped
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            return true;
        }
        
        return false;
    }
}