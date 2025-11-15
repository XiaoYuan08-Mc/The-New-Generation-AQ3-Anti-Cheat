package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.physics.PhysicsEngine;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 飞行检查
 * Fly check
 */
public class FlyCheck extends Check {
    
    public FlyCheck() {
        super("Fly", "检测飞行作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        // 检查是否启用（动态读取配置）
        // Check if enabled (dynamically read configuration)
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.movement.fly.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        // 获取最大违规次数（动态读取配置）
        // Get maximum violations (dynamically read configuration)
        int maxViolations = AQ3API.getInstance().getConfigManager()
            .getInt("checks.movement.fly.max_violations", 10);
        
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
        
        // 检查玩家是否在特殊状态下（如创造模式飞行）
        // Check if player is in special state (e.g. creative mode flying)
        if (isInSpecialState(playerData)) {
            return false;
        }
        
        // 检查玩家是否在梯子、藤蔓或其他特殊方块上
        // Check if player is on ladder, vine or other special blocks
        // 这里简化处理，实际项目中需要检查玩家站立的方块类型
        // Simplified handling, in a real project you'd check the block type the player is standing on
        
        // 使用物理引擎检查玩家是否可能在飞行
        // Use physics engine to check if player might be flying
        boolean isFlying = PhysicsEngine.isFlying(playerData, playerData.isOnGround());
        
        // 检查是否为正常跳跃
        // Check if it's normal jumping
        boolean isNormalJumping = isNormalJumping(playerData);
        
        // 如果是正常跳跃，则不判定为飞行作弊
        // If it's normal jumping, don't判定 of flight cheating
        if (isNormalJumping) {
            return false;
        }
        
        // 检查是否在合理范围内移动，使用灵敏度调整的阈值
        // Check if moving within reasonable bounds, using sensitivity-adjusted threshold
        boolean withinBounds = PhysicsEngine.isMovingWithinBoundsWithThreshold(playerData, thresholdMultiplier);
        
        // 如果玩家被判定为在飞行且移动超出合理范围，则可能是作弊
        // If player is judged to be flying and movement exceeds reasonable bounds, may be cheating
        return isFlying && !withinBounds;
    }
    
    /**
     * 检查玩家是否在特殊状态
     * Check if player is in special state
     */
    private boolean isInSpecialState(PlayerData playerData) {
        // 检查是否在水中或岩浆中
        // Check if in water or lava
        double yDecimal = playerData.getY() - Math.floor(playerData.getY());
        if (yDecimal > 0.9 || yDecimal < 0.1) {
            // 可能在液体中
            // Possibly in liquid
            return true;
        }
        
        // 检查是否在爬梯子或藤蔓
        // Check if climbing ladder or vine
        if (!playerData.isOnGround() && !playerData.wasOnGround()) {
            double verticalDistance = playerData.getVerticalDistance();
            // 如果垂直移动速度较慢，可能在爬梯子
            // If vertical movement speed is slow, may be climbing
            if (Math.abs(verticalDistance) < 0.2) {
                return true;
            }
        }
        
        // 检查是否刚刚跳跃，这是完全正常的操作
        // Check if just jumped, which is completely normal operation
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            // 刚刚跳跃，不应视为飞行作弊
            // Just jumped, should not be considered as fly cheating
            return true;
        }
        
        // 检查是否在蹲下状态切换
        // Check if toggling sneak state
        if (playerData.isSneaking() != playerData.wasSneaking()) {
            return true;
        }
        
        // 检查是否处于创造模式
        // Check if in creative mode
        // 注意：在实际实现中，我们需要从Bukkit获取玩家的游戏模式
        // Note: In actual implementation, we need to get player's game mode from Bukkit
        
        // 检查是否处于末影珍珠冷却或其他传送效果期间
        // Check if in ender pearl cooldown or other teleport effects period
        // 这可以通过检查短时间内是否有异常的位置变化来实现
        // This can be implemented by checking for abnormal position changes in a short time
        
        return false;
    }
    
    /**
     * 检查是否为正常跳跃而不是飞行
     * Check if it's normal jumping rather than flying
     */
    private boolean isNormalJumping(PlayerData playerData) {
        // 如果玩家刚刚从地面跳起，这是正常的
        // If player just jumped from ground, this is normal
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            return true;
        }
        
        // 检查垂直速度是否符合正常跳跃规律
        // Check if vertical speed conforms to normal jump patterns
        double deltaY = playerData.getY() - playerData.getLastY();
        
        // 正常跳跃后的上升阶段
        // Ascending phase after normal jump
        if (deltaY > 0 && deltaY <= PhysicsEngine.JUMP_POWER) { // 0.42是Minecraft中正常的跳跃力量 / 0.42 is normal jump power in Minecraft
            // 检查是否是正常跳跃的高度增加
            // Check if it's normal height increase from jumping
            double expectedDeltaY = PhysicsEngine.JUMP_POWER - PhysicsEngine.GRAVITY; // 第一帧跳跃后的预期下降值
            if (Math.abs(deltaY - expectedDeltaY) < 0.05) {
                return true;
            }
            return true;
        }
        
        // 正常重力影响下的下降阶段
        // Descending phase under normal gravity
        if (deltaY < 0 && deltaY >= -PhysicsEngine.GRAVITY) { // -0.08是Minecraft中正常的重力 / -0.08 is normal gravity in Minecraft
            return true;
        }
        
        // 检查连续的正常重力下降
        // Check for consecutive normal gravity descents
        if (deltaY < 0 && Math.abs(deltaY + PhysicsEngine.GRAVITY) < 0.01) {
            // 符合正常重力规律
            // Conforms to normal gravity pattern
            return true;
        }
        
        // 检查是否在跳跃后的第二帧
        // Check if it's the second frame after jumping
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            // 上一帧是从地面跳起的
            // Previous frame was jumping from ground
            double lastDeltaY = playerData.getLastY() - playerData.getLastLastY();
            if (lastDeltaY > 0.41 && lastDeltaY < 0.43) {
                // 上一帧确实是跳跃，当前帧应该是重力影响的第一帧下降
                // Previous frame was indeed jumping, current frame should be first descent frame affected by gravity
                if (Math.abs(deltaY - (lastDeltaY - PhysicsEngine.GRAVITY)) < 0.01) {
                    return true;
                }
            }
        }
        
        // 检查是否符合重力连续下降规律
        // Check if conforms to continuous gravity descent pattern
        if (!playerData.isOnGround() && playerData.getLastY() > playerData.getLastLastY()) {
            // 上一帧在上升，当前帧应该受到重力影响
            double expectedDeltaY = (playerData.getLastY() - playerData.getLastLastY()) - PhysicsEngine.GRAVITY;
            if (Math.abs(deltaY - expectedDeltaY) < 0.01) {
                return true;
            }
        }
        
        return false;
    }
}