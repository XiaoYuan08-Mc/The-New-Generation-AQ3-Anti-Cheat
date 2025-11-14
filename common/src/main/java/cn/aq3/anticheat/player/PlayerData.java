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
}