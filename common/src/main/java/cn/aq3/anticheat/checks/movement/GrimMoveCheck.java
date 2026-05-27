package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.ArrayList;
import java.util.List;

/**
 * GrimAC风格的综合运动检测
 * 综合验证玩家的移动是否遵循Minecraft物理规则
 */
public class GrimMoveCheck extends Check {
    
    private static final double GRAVITY = 0.08;
    private static final double JUMP_POWER = 0.42;
    private static final double HORIZONTAL_FRICTION = 0.91;
    private static final double AIR_FRICTION = 0.98;
    
    private static final double MAX_JUMP_HEIGHT = 1.25;
    private static final double MAX_FALL_SPEED = 3.92;
    
    private final List<Double> lastVelocities = new ArrayList<>();
    private static final int VELOCITY_HISTORY_SIZE = 10;
    
    public GrimMoveCheck() {
        super("GrimMove", "GrimAC综合运动检测", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        if (playerData == null) {
            return false;
        }
        
        // 检查玩家是否刚刚加入游戏
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        // 验证重力规则
        if (checkGravityViolation(playerData)) {
            return true;
        }
        
        // 验证跳跃规则
        if (checkJumpViolation(playerData)) {
            return true;
        }
        
        // 验证移动速度规则
        if (checkSpeedViolation(playerData)) {
            return true;
        }
        
        // 验证地面状态
        if (checkGroundStateViolation(playerData)) {
            return true;
        }
        
        return false;
    }
    
    private boolean checkGravityViolation(PlayerData playerData) {
        double velocityY = playerData.getVelocityY();
        
        // 记录速度历史
        lastVelocities.add(velocityY);
        if (lastVelocities.size() > VELOCITY_HISTORY_SIZE) {
            lastVelocities.remove(0);
        }
        
        // 检查是否在地面上
        if (playerData.isOnGround()) {
            // 地面上的垂直速度应该接近0或很小
            if (velocityY > 0.1) {
                return true; // 在地面上但有向上速度
            }
            return false;
        }
        
        // 在空中，应该受到重力影响
        if (!playerData.wasOnGround()) {
            // 检查是否在液体中
            // 简化处理：如果速度合理，不报错
            if (Math.abs(velocityY) > MAX_FALL_SPEED) {
                return true; // 速度过快
            }
        }
        
        return false;
    }
    
    private boolean checkJumpViolation(PlayerData playerData) {
        boolean wasOnGround = playerData.wasOnGround();
        boolean isOnGround = playerData.isOnGround();
        
        // 跳跃：刚从地面起跳
        if (!wasOnGround && isOnGround) {
            double velocityY = playerData.getVelocityY();
            
            // 正常跳跃速度应该在0.42左右
            if (velocityY < JUMP_POWER - 0.1) {
                return true; // 跳跃速度异常
            }
            
            // 检查跳跃高度
            double yDiff = playerData.getY() - playerData.getLastY();
            if (yDiff > MAX_JUMP_HEIGHT) {
                return true; // 跳跃过高
            }
        }
        
        return false;
    }
    
    private boolean checkSpeedViolation(PlayerData playerData) {
        double velocityX = playerData.getVelocityX();
        double velocityZ = playerData.getVelocityZ();
        
        // 计算水平速度
        double horizontalSpeed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        
        // 最大水平速度阈值（包含鞘翅等）
        double maxSpeed = playerData.isFlyingWithElytra() ? 1.5 : 0.35;
        
        // 冲刺时速度可以更高
        if (playerData.isSprinting()) {
            maxSpeed *= 1.3;
        }
        
        // 蹲下时速度应该更慢
        if (playerData.isSneaking()) {
            maxSpeed *= 0.3;
        }
        
        // 在空中时速度限制不同
        if (!playerData.isOnGround()) {
            maxSpeed *= 1.1;
        }
        
        if (horizontalSpeed > maxSpeed * 1.2) { // 20%容差
            return true;
        }
        
        return false;
    }
    
    private boolean checkGroundStateViolation(PlayerData playerData) {
        // 检查onGround状态是否与实际情况匹配
        
        double velocityY = playerData.getVelocityY();
        double deltaY = playerData.getY() - playerData.getLastY();
        
        // 如果onGround为true，但有明显的垂直移动，可能是状态错误
        if (playerData.isOnGround() && Math.abs(deltaY) > 0.01) {
            // 允许小的误差
            if (Math.abs(deltaY) > 0.1) {
                return true;
            }
        }
        
        return false;
    }
    
    public void resetVelocityHistory() {
        lastVelocities.clear();
    }
}
