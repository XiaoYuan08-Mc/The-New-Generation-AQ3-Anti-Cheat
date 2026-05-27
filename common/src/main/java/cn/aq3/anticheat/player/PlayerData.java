package cn.aq3.anticheat.player;

import java.util.UUID;

/**
 * 玩家数据类，存储每个玩家的反作弊相关数据
 * Player data class, storing anti-cheat related data for each player
 */
public class PlayerData {
    private final UUID uuid;
    private final String name;
    
    // 运动数据
    // Movement data
    private double lastX, lastY, lastZ;
    private double x, y, z;
    private float lastYaw, lastPitch;
    private float yaw, pitch;
    private boolean onGround;
    private boolean wasOnGround; // 上一时刻是否在地面
    private boolean isSneaking; // 是否蹲下
    private boolean wasSneaking; // 上一时刻是否蹲下
    
    // 速度数据
    // Velocity data
    private double velocityX, velocityY, velocityZ;
    private double lastVelocityX, lastVelocityY, lastVelocityZ;
    
    // 连接数据
    // Connection data
    private long joinTime;
    private long lastPacketTime;
    private long lastAttackTime; // 上次攻击时间
    private long lastOnGroundTime; // 上次在地面的时间
    private long lastBreakStartTime; // 上次开始破坏方块的时间
    
    // 检查结果
    // Check results
    private int violationLevel;
    
    // 物品使用时间
    // Item use time
    private long lastUseTime = 0;
    private double lastLastY; // 上上一帧的Y坐标 / Y coordinate of the frame before last
    private double fallDistance; // 摔落距离 / Fall distance
    
    // 鞘翅飞行
    // Elytra flying
    private boolean flyingWithElytra;
    
    // 传送相关
    // Teleport related
    private long lastTeleportTime;
    private long lastEnderPearlTime;
    
    // 玩家属性
    // Player attributes
    private double health;
    private double lastHealth;
    private double experience;
    private double lastExperience;
    private int foodLevel;
    private int lastFoodLevel;
    
    // 时间戳
    // Timestamps
    private long lastHealTime;
    private long lastEatTime;
    private long lastDigTime;
    
    // 方块计数
    // Block counters
    private int blockPlaceCount;
    private long lastPlaceCheckTime;
    private int blockBreakCount;
    private long lastBreakCheckTime;
    
    // 飞行和冲刺状态
    // Flying and sprinting state
    private boolean flying;
    private boolean sprinting;
    
    // 其他时间戳
    // Other timestamps
    private long lastBlockPlaceTime;
    private long lastInteractTime;
    
    // 速度相关字段
    // Velocity related fields
    private double velocityTakenX;
    private double velocityTakenY;
    private double velocityTakenZ;
    private long lastVelocityTime;
    private boolean velocityModified;
    
    // 攻击相关字段
    // Attack related fields
    private int attackCount;
    private long lastAttackEntityId;
    
    // 移动历史记录
    // Movement history
    private final double[] positionHistoryX = new double[20];
    private final double[] positionHistoryY = new double[20];
    private final double[] positionHistoryZ = new double[20];
    private int historyIndex = 0;
    
    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.joinTime = System.currentTimeMillis();
        this.lastPacketTime = System.currentTimeMillis();
        this.lastAttackTime = 0;
        this.lastOnGroundTime = System.currentTimeMillis();
        this.lastBreakStartTime = 0;
        // 初始化位置为0，实际使用时应该从事件中获取真实位置
        // Initialize position to 0, in actual use should get real position from events
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.lastX = 0;
        this.lastY = 0;
        this.lastZ = 0;
        this.lastLastY = 0; // 初始化上上一帧Y坐标 / Initialize frame before last Y coordinate
        this.yaw = 0;
        this.pitch = 0;
        this.lastYaw = 0;
        this.lastPitch = 0;
        this.onGround = false;
        this.wasOnGround = false;
        this.isSneaking = false;
        this.wasSneaking = false;
        this.violationLevel = 0;
        this.lastUseTime = 0;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.lastVelocityX = 0;
        this.lastVelocityY = 0;
        this.lastVelocityZ = 0;
        this.fallDistance = 0;
        
        // 初始化新增字段
        this.flyingWithElytra = false;
        this.lastTeleportTime = 0;
        this.lastEnderPearlTime = 0;
        this.health = 20.0;
        this.lastHealth = 20.0;
        this.experience = 0;
        this.lastExperience = 0;
        this.foodLevel = 20;
        this.lastFoodLevel = 20;
        this.lastHealTime = 0;
        this.lastEatTime = 0;
        this.lastDigTime = 0;
        this.blockPlaceCount = 0;
        this.lastPlaceCheckTime = 0;
        this.blockBreakCount = 0;
        this.lastBreakCheckTime = 0;
        
        // 初始化飞行和冲刺状态
        this.flying = false;
        this.sprinting = false;
        this.lastBlockPlaceTime = 0;
        this.lastInteractTime = 0;
        
        // 初始化速度相关字段
        this.velocityTakenX = 0;
        this.velocityTakenY = 0;
        this.velocityTakenZ = 0;
        this.lastVelocityTime = 0;
        this.velocityModified = false;
        
        // 初始化攻击相关字段
        this.attackCount = 0;
        this.lastAttackEntityId = -1;
        
        // 初始化移动历史
        for (int i = 0; i < 20; i++) {
            this.positionHistoryX[i] = 0;
            this.positionHistoryY[i] = 0;
            this.positionHistoryZ[i] = 0;
        }
    }
    
    // Getters and setters
    public UUID getUuid() {
        return uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public double getLastX() {
        return lastX;
    }
    
    public void setLastX(double lastX) {
        this.lastX = lastX;
    }
    
    public double getLastY() {
        return lastY;
    }
    
    public void setLastY(double lastY) {
        this.lastY = lastY;
    }
    
    public double getLastZ() {
        return lastZ;
    }
    
    public void setLastZ(double lastZ) {
        this.lastZ = lastZ;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getLastYaw() {
        return lastYaw;
    }
    
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public float getLastPitch() {
        return lastPitch;
    }
    
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    public boolean isOnGround() {
        return onGround;
    }
    
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        // 更新上次在地面的时间
        // Update last on ground time
        if (onGround) {
            this.lastOnGroundTime = System.currentTimeMillis();
        }
    }
    
    public boolean wasOnGround() {
        return wasOnGround;
    }
    
    public void setWasOnGround(boolean wasOnGround) {
        this.wasOnGround = wasOnGround;
    }
    
    public boolean isSneaking() {
        return isSneaking;
    }
    
    public void setSneaking(boolean sneaking) {
        this.isSneaking = sneaking;
    }
    
    public boolean wasSneaking() {
        return wasSneaking;
    }
    
    public void setWasSneaking(boolean wasSneaking) {
        this.wasSneaking = wasSneaking;
    }
    
    public long getJoinTime() {
        return joinTime;
    }
    
    public long getLastPacketTime() {
        return lastPacketTime;
    }
    
    public void setLastPacketTime(long lastPacketTime) {
        this.lastPacketTime = lastPacketTime;
    }
    
    public int getViolationLevel() {
        return violationLevel;
    }
    
    public void setViolationLevel(int violationLevel) {
        this.violationLevel = violationLevel;
    }
    
    public long getLastUseTime() {
        return lastUseTime;
    }
    
    public void setLastUseTime(long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }
    
    public double getLastLastY() {
        return lastLastY;
    }
    
    public double getVelocityX() {
        return velocityX;
    }
    
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }
    
    public double getVelocityY() {
        return velocityY;
    }
    
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
    
    public double getVelocityZ() {
        return velocityZ;
    }
    
    public void setVelocityZ(double velocityZ) {
        this.velocityZ = velocityZ;
    }
    
    public double getLastVelocityX() {
        return lastVelocityX;
    }
    
    public void setLastVelocityX(double lastVelocityX) {
        this.lastVelocityX = lastVelocityX;
    }
    
    public double getLastVelocityY() {
        return lastVelocityY;
    }
    
    public void setLastVelocityY(double lastVelocityY) {
        this.lastVelocityY = lastVelocityY;
    }
    
    public double getLastVelocityZ() {
        return lastVelocityZ;
    }
    
    public void setLastVelocityZ(double lastVelocityZ) {
        this.lastVelocityZ = lastVelocityZ;
    }
    
    public long getLastAttackTime() {
        return lastAttackTime;
    }
    
    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }
    
    public long getLastOnGroundTime() {
        return lastOnGroundTime;
    }
    
    public void setLastOnGroundTime(long lastOnGroundTime) {
        this.lastOnGroundTime = lastOnGroundTime;
    }
    
    public long getLastBreakStartTime() {
        return lastBreakStartTime;
    }
    
    public void setLastBreakStartTime(long lastBreakStartTime) {
        this.lastBreakStartTime = lastBreakStartTime;
    }
    
    public double getFallDistance() {
        return fallDistance;
    }
    
    public void setFallDistance(double fallDistance) {
        this.fallDistance = fallDistance;
    }
    
    /**
     * 更新位置信息
     * Update position information
     */
    public void updatePosition(double x, double y, double z, boolean onGround) {
        // 更新速度信息
        // Update velocity information
        this.lastVelocityX = this.velocityX;
        this.lastVelocityY = this.velocityY;
        this.lastVelocityZ = this.velocityZ;
        
        this.velocityX = x - this.x;
        this.velocityY = y - this.y;
        this.velocityZ = z - this.z;
        
        // 更新位置历史记录
        // Update position history
        this.lastLastY = this.lastY; // 保存上一帧的Y坐标 / Save previous frame's Y coordinate
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.wasOnGround = this.onGround;
        this.onGround = onGround;
        
        // 更新上次在地面的时间
        // Update last on ground time
        if (onGround) {
            this.lastOnGroundTime = System.currentTimeMillis();
        }
        
        // 更新摔落距离
        // Update fall distance
        if (onGround) {
            this.fallDistance = 0;
        } else if (y < lastY) {
            this.fallDistance += (lastY - y);
        }
        
        // 保存移动历史记录
        // Save movement history
        this.positionHistoryX[historyIndex] = x;
        this.positionHistoryY[historyIndex] = y;
        this.positionHistoryZ[historyIndex] = z;
        this.historyIndex = (historyIndex + 1) % 20;
    }
    
    // Velocity related getters and setters
    public double getVelocityTakenX() {
        return velocityTakenX;
    }
    
    public void setVelocityTakenX(double velocityTakenX) {
        this.velocityTakenX = velocityTakenX;
    }
    
    public double getVelocityTakenY() {
        return velocityTakenY;
    }
    
    public void setVelocityTakenY(double velocityTakenY) {
        this.velocityTakenY = velocityTakenY;
    }
    
    public double getVelocityTakenZ() {
        return velocityTakenZ;
    }
    
    public void setVelocityTakenZ(double velocityTakenZ) {
        this.velocityTakenZ = velocityTakenZ;
    }
    
    public long getLastVelocityTime() {
        return lastVelocityTime;
    }
    
    public void setLastVelocityTime(long lastVelocityTime) {
        this.lastVelocityTime = lastVelocityTime;
    }
    
    public boolean isVelocityModified() {
        return velocityModified;
    }
    
    public void setVelocityModified(boolean velocityModified) {
        this.velocityModified = velocityModified;
    }
    
    // Attack related getters and setters
    public int getAttackCount() {
        return attackCount;
    }
    
    public void setAttackCount(int attackCount) {
        this.attackCount = attackCount;
    }
    
    public long getLastAttackEntityId() {
        return lastAttackEntityId;
    }
    
    public void setLastAttackEntityId(long lastAttackEntityId) {
        this.lastAttackEntityId = lastAttackEntityId;
    }
    
    // Position history getters
    public double[] getPositionHistoryX() {
        return positionHistoryX;
    }
    
    public double[] getPositionHistoryY() {
        return positionHistoryY;
    }
    
    public double[] getPositionHistoryZ() {
        return positionHistoryZ;
    }
    
    public int getHistoryIndex() {
        return historyIndex;
    }
    
    /**
     * 更新视角信息
     * Update look information
     */
    public void updateLook(float yaw, float pitch) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    /**
     * 更新蹲下状态
     * Update sneaking state
     */
    public void updateSneaking(boolean isSneaking) {
        this.wasSneaking = this.isSneaking;
        this.isSneaking = isSneaking;
    }
    
    /**
     * 计算水平移动距离
     * Calculate horizontal movement distance
     */
    public double getHorizontalDistance() {
        double deltaX = x - lastX;
        double deltaZ = z - lastZ;
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }
    
    /**
     * 获取垂直移动距离
     * Get vertical movement distance
     */
    public double getVerticalDistance() {
        return y - lastY;
    }
    
    // Elytra flying methods
    public boolean isFlyingWithElytra() {
        return flyingWithElytra;
    }
    
    public void setFlyingWithElytra(boolean flyingWithElytra) {
        this.flyingWithElytra = flyingWithElytra;
    }
    
    // Teleport methods
    public long getLastTeleportTime() {
        return lastTeleportTime;
    }
    
    public void setLastTeleportTime(long lastTeleportTime) {
        this.lastTeleportTime = lastTeleportTime;
    }
    
    public long getLastEnderPearlTime() {
        return lastEnderPearlTime;
    }
    
    public void setLastEnderPearlTime(long lastEnderPearlTime) {
        this.lastEnderPearlTime = lastEnderPearlTime;
    }
    
    // Health methods
    public double getHealth() {
        return health;
    }
    
    public void setHealth(double health) {
        this.lastHealth = this.health;
        this.health = health;
    }
    
    public double getLastHealth() {
        return lastHealth;
    }
    
    public long getLastHealTime() {
        return lastHealTime;
    }
    
    public void setLastHealTime(long lastHealTime) {
        this.lastHealTime = lastHealTime;
    }
    
    // Experience methods
    public double getExperience() {
        return experience;
    }
    
    public void setExperience(double experience) {
        this.lastExperience = this.experience;
        this.experience = experience;
    }
    
    public double getLastExperience() {
        return lastExperience;
    }
    
    // Food methods
    public int getFoodLevel() {
        return foodLevel;
    }
    
    public void setFoodLevel(int foodLevel) {
        this.lastFoodLevel = this.foodLevel;
        this.foodLevel = foodLevel;
    }
    
    public int getLastFoodLevel() {
        return lastFoodLevel;
    }
    
    public long getLastEatTime() {
        return lastEatTime;
    }
    
    public void setLastEatTime(long lastEatTime) {
        this.lastEatTime = lastEatTime;
    }
    
    // Dig methods
    public long getLastDigTime() {
        return lastDigTime;
    }
    
    public void setLastDigTime(long lastDigTime) {
        this.lastDigTime = lastDigTime;
    }
    
    // Block place methods
    public int getBlockPlaceCount() {
        return blockPlaceCount;
    }
    
    public void setBlockPlaceCount(int blockPlaceCount) {
        this.blockPlaceCount = blockPlaceCount;
    }
    
    public long getLastPlaceCheckTime() {
        return lastPlaceCheckTime;
    }
    
    public void setLastPlaceCheckTime(long lastPlaceCheckTime) {
        this.lastPlaceCheckTime = lastPlaceCheckTime;
    }
    
    // Block break methods
    public int getBlockBreakCount() {
        return blockBreakCount;
    }
    
    public void setBlockBreakCount(int blockBreakCount) {
        this.blockBreakCount = blockBreakCount;
    }
    
    public long getLastBreakCheckTime() {
        return lastBreakCheckTime;
    }
    
    public void setLastBreakCheckTime(long lastBreakCheckTime) {
        this.lastBreakCheckTime = lastBreakCheckTime;
    }
    
    // Flying methods
    public boolean isFlying() {
        return flying;
    }
    
    public void setFlying(boolean flying) {
        this.flying = flying;
    }
    
    // Sprinting methods
    public boolean isSprinting() {
        return sprinting;
    }
    
    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }
    
    // Block place time methods
    public long getLastBlockPlaceTime() {
        return lastBlockPlaceTime;
    }
    
    public void setLastBlockPlaceTime(long lastBlockPlaceTime) {
        this.lastBlockPlaceTime = lastBlockPlaceTime;
    }
    
    // Interact time methods
    public long getLastInteractTime() {
        return lastInteractTime;
    }
    
    public void setLastInteractTime(long lastInteractTime) {
        this.lastInteractTime = lastInteractTime;
    }
}