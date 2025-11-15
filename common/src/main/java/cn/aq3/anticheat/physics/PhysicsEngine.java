package cn.aq3.anticheat.physics;

import cn.aq3.anticheat.player.PlayerData;

/**
 * 物理引擎 - 模拟玩家在Minecraft中的运动
 * Physics Engine - Simulates player movement in Minecraft
 */
public class PhysicsEngine {
    
    // 基本物理常量
    // Basic physics constants
    public static final double GRAVITY = 0.08; // 将访问权限改为public / Change access to public
    public static final double JUMP_POWER = 0.42;
    private static final double MOVEMENT_SPEED = 0.215;
    private static final double SPRINT_MULTIPLIER = 1.3;
    private static final double WATER_MOVEMENT_MODIFIER = 0.8;
    private static final double COBWEB_SLOWDOWN = 0.25;
    private static final double SNEAKING_MODIFIER = 0.3;
    private static final double SOUL_SPEED_BOOST = 0.01;
    private static final double HONEY_BLOCK_SLOWDOWN = 0.4;
    
    // 环境检测阈值
    // Environmental detection thresholds
    private static final double WATER_THRESHOLD = 0.9;
    private static final double LAVA_THRESHOLD = 0.9;
    
    /**
     * 计算玩家在正常情况下的预期位置
     * Calculate expected player position under normal conditions
     */
    public static double[] calculateExpectedPosition(PlayerData playerData, 
                                                     boolean isSprinting, 
                                                     boolean isSneaking,
                                                     boolean isInWater, 
                                                     boolean isInLava,
                                                     boolean isInCobweb,
                                                     boolean isInHoneyBlock,
                                                     boolean isOnSoulSpeedBlock,
                                                     boolean isOnGround) {
        double expectedX = playerData.getLastX();
        double expectedY = playerData.getLastY();
        double expectedZ = playerData.getLastZ();
        
        // 计算水平移动
        // Calculate horizontal movement
        double moveSpeed = MOVEMENT_SPEED;
        
        // 应用移动修饰符
        // Apply movement modifiers
        if (isSprinting) {
            moveSpeed *= SPRINT_MULTIPLIER;
        }
        if (isSneaking) {
            moveSpeed *= SNEAKING_MODIFIER;
        }
        if (isInWater) {
            moveSpeed *= WATER_MOVEMENT_MODIFIER;
        }
        if (isInCobweb) {
            moveSpeed *= COBWEB_SLOWDOWN;
        }
        if (isInHoneyBlock) {
            moveSpeed *= HONEY_BLOCK_SLOWDOWN;
        }
        if (isOnSoulSpeedBlock) {
            moveSpeed += SOUL_SPEED_BOOST;
        }
        
        // 应用移动速度到坐标变化
        // Apply movement speed to coordinate changes
        double deltaX = playerData.getX() - playerData.getLastX();
        double deltaZ = playerData.getZ() - playerData.getLastZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        
        if (horizontalDistance > 0) {
            expectedX = playerData.getLastX() + (deltaX / horizontalDistance) * moveSpeed;
            expectedZ = playerData.getLastZ() + (deltaZ / horizontalDistance) * moveSpeed;
        }
        
        // 计算垂直移动
        // Calculate vertical movement
        if (isOnGround) {
            // 在地面上，可能跳跃
            // On ground, may jump
            expectedY += JUMP_POWER;
        } else {
            // 在空中，受重力影响
            // In air, affected by gravity
            expectedY -= GRAVITY;
            
            // 应用液体浮力
            // Apply liquid buoyancy
            if (isInWater) {
                expectedY += 0.02; // 水中的浮力 / Buoyancy in water
            } else if (isInLava) {
                expectedY += 0.02; // 岩浆中的浮力 / Buoyancy in lava
            }
        }
        
        return new double[]{expectedX, expectedY, expectedZ};
    }
    
    /**
     * 检查玩家移动是否符合物理规律
     * Check if player movement conforms to physics
     */
    public static boolean isValidMovement(PlayerData playerData,
                                         double expectedX, 
                                         double expectedY, 
                                         double expectedZ) {
        double deltaX = Math.abs(playerData.getX() - expectedX);
        double deltaY = Math.abs(playerData.getY() - expectedY);
        double deltaZ = Math.abs(playerData.getZ() - expectedZ);
        
        // 允许一定的误差范围
        // Allow for some margin of error
        double tolerance = 0.1;
        
        return deltaX <= tolerance && deltaY <= tolerance && deltaZ <= tolerance;
    }
    
    /**
     * 检查玩家移动是否符合物理规律（带阈值调整）
     * Check if player movement conforms to physics (with threshold adjustment)
     */
    public static boolean isValidMovementWithThreshold(PlayerData playerData,
                                                      double expectedX, 
                                                      double expectedY, 
                                                      double expectedZ,
                                                      double thresholdMultiplier) {
        double deltaX = Math.abs(playerData.getX() - expectedX);
        double deltaY = Math.abs(playerData.getY() - expectedY);
        double deltaZ = Math.abs(playerData.getZ() - expectedZ);
        
        // 允许一定的误差范围，根据阈值调整
        // Allow for some margin of error, adjusted by threshold
        double tolerance = 0.1 * thresholdMultiplier;
        
        // 对于蹲下状态，允许更大的误差范围
        // For sneaking state, allow larger margin of error
        if (playerData.isSneaking() || playerData.wasSneaking()) {
            tolerance *= 2.0; // 增加蹲下时的容错率 / Increase tolerance when sneaking
        }
        
        // 对于跳跃，允许更大的垂直误差范围
        // For jumping, allow larger vertical margin of error
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            tolerance *= 4.0; // 跳跃时增加容错率 / Increase tolerance when jumping
        }
        
        // 对于在空中的玩家，允许更大的误差范围
        // For players in the air, allow larger margin of error
        if (!playerData.isOnGround()) {
            tolerance *= 1.5;
        }
        
        return deltaX <= tolerance && deltaY <= tolerance && deltaZ <= tolerance;
    }
    
    /**
     * 检查玩家是否可能在飞行
     * Check if player might be flying
     */
    public static boolean isFlying(PlayerData playerData, boolean isOnGround) {
        // 如果玩家在地面上，则不可能在飞行
        // If player is on ground, cannot be flying
        if (isOnGround) {
            return false;
        }
        
        // 如果玩家刚刚跳跃，则不判定为飞行
        // If player just jumped, don't判定 of flight
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            return false;
        }
        
        // 垂直位置变化
        // Vertical position change
        double deltaY = playerData.getY() - playerData.getLastY();
        
        // 检查是否在液体中
        // Check if in liquid
        double yDecimal = playerData.getY() - Math.floor(playerData.getY());
        if (yDecimal < 0.1 || yDecimal > 0.9) {
            // 可能在液体中，不判定为飞行
            // Possibly in liquid, don't判定 of flight
            return false;
        }
        
        // 检查是否在爬梯子或藤蔓上
        // Check if climbing ladder or vine
        if (!playerData.isOnGround() && !playerData.wasOnGround()) {
            double verticalDistance = playerData.getVerticalDistance();
            // 如果垂直移动速度较慢，可能在爬梯子或藤蔓
            // If vertical movement speed is slow, may be climbing ladder or vine
            if (Math.abs(verticalDistance) < 0.2) {
                return false;
            }
            
            // 如果玩家正在向上移动但速度很慢，可能在爬墙或使用其他合法机制
            // If player is moving upward but slowly, may be climbing wall or using other legal mechanics
            if (verticalDistance > 0 && verticalDistance < 0.15) {
                return false;
            }
        }
        
        // 检查连续的异常移动
        // Check for consecutive abnormal movements
        // 这可以帮助区分真正的飞行作弊和正常的跳跃
        // This helps differentiate real fly hacks from normal jumps
        
        // 检查垂直速度是否符合重力规律
        // Check if vertical speed conforms to gravity
        // 正常重力影响下的垂直速度应该接近 -0.08
        // Normal gravity-affected vertical speed should be close to -0.08
        
        // 如果玩家持续保持正向垂直速度或者几乎没有下降，这更可能是飞行作弊
        // If player consistently maintains positive vertical velocity or barely descends, this is more likely fly cheating
        boolean isMaintainingHeight = deltaY > -0.01 && deltaY < 0.05;
        boolean isRising = deltaY > 0.05;
        
        // 结合多个条件判断是否真的在飞行
        // Combine multiple conditions to judge if really flying
        return (isMaintainingHeight || isRising) && 
               Math.abs(deltaY + GRAVITY) > 0.15; // 更宽松的阈值 / More lenient threshold
    }
    
    /**
     * 检查玩家是否在合理范围内移动
     * Check if player is moving within reasonable bounds
     */
    public static boolean isMovingWithinBounds(PlayerData playerData) {
        double deltaX = Math.abs(playerData.getX() - playerData.getLastX());
        double deltaY = Math.abs(playerData.getY() - playerData.getLastY());
        double deltaZ = Math.abs(playerData.getZ() - playerData.getLastZ());
        
        // 检查移动速度是否超出合理范围
        // Check if movement speed exceeds reasonable bounds
        double maxHorizontalSpeed = 1.0;
        double maxVerticalSpeed = 1.0;
        
        // 蹲下时移动速度较慢
        // Movement speed is slower when sneaking
        if (playerData.isSneaking() || playerData.wasSneaking()) {
            maxHorizontalSpeed *= 0.3;
        }
        
        // 跳跃时垂直速度较快
        // Vertical speed is faster when jumping
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            maxVerticalSpeed *= 3.0;
        }
        
        return deltaX <= maxHorizontalSpeed && deltaY <= maxVerticalSpeed && deltaZ <= maxHorizontalSpeed;
    }
    
    /**
     * 检查玩家是否在合理范围内移动（带阈值调整）
     * Check if player is moving within reasonable bounds (with threshold adjustment)
     */
    public static boolean isMovingWithinBoundsWithThreshold(PlayerData playerData, double thresholdMultiplier) {
        double deltaX = Math.abs(playerData.getX() - playerData.getLastX());
        double deltaY = Math.abs(playerData.getY() - playerData.getLastY());
        double deltaZ = Math.abs(playerData.getZ() - playerData.getLastZ());
        
        // 检查移动速度是否超出合理范围，根据阈值调整
        // Check if movement speed exceeds reasonable bounds, adjusted by threshold
        double maxHorizontalSpeed = 1.0 * thresholdMultiplier;
        double maxVerticalSpeed = 1.0 * thresholdMultiplier;
        
        // 蹲下时移动速度较慢
        // Movement speed is slower when sneaking
        if (playerData.isSneaking() || playerData.wasSneaking()) {
            maxHorizontalSpeed *= 0.3;
        }
        
        // 跳跃时垂直速度较快
        // Vertical speed is faster when jumping
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            maxVerticalSpeed *= 4.0; // 增加跳跃时的容错率 / Increase tolerance for jumping
        }
        
        // 检查是否在爬升过程中（可能是在爬梯子）
        // Check if ascending (may be climbing a ladder)
        if (deltaY > 0 && deltaY < 0.2) {
            maxVerticalSpeed *= 2.0; // 对缓慢上升给予更多容忍 / Give more tolerance for slow ascent
        }
        
        // 特殊处理：如果从地面跳跃，允许更大的垂直移动
        // Special handling: Allow greater vertical movement if jumping from ground
        if (!playerData.isOnGround() && playerData.wasOnGround()) {
            maxVerticalSpeed = Math.max(maxVerticalSpeed, 0.5 * thresholdMultiplier); // 至少允许0.5的垂直移动 / At least allow 0.5 vertical movement
        }
        
        // 检查是否处于连续的重力下降状态
        // Check if in consecutive gravity descent state
        if (!playerData.isOnGround() && playerData.wasOnGround() && playerData.getLastY() > playerData.getLastLastY()) {
            // 第一帧跳跃后应该受到重力影响
            double expectedDeltaY = playerData.getLastY() - playerData.getLastLastY() - GRAVITY;
            if (Math.abs(deltaY - expectedDeltaY) < 0.01) {
                // 符合重力规律，不应该是飞行
                // Conforms to gravity, should not be flying
                maxVerticalSpeed = Math.max(maxVerticalSpeed, Math.abs(expectedDeltaY) * thresholdMultiplier);
            }
        }
        
        return deltaX <= maxHorizontalSpeed && deltaY <= maxVerticalSpeed && deltaZ <= maxHorizontalSpeed;
    }
}