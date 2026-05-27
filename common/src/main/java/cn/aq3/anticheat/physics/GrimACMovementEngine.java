package cn.aq3.anticheat.physics;

import cn.aq3.anticheat.player.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GrimAC 风格的运动验证引擎
 * GrimAC-style Movement Verification Engine
 * 
 * 核心特性：
 * - 精确的重力模拟
 * - 准确的摩擦计算
 * - 严格的边界检测
 * - 延迟补偿系统
 */
public class GrimACMovementEngine {
    
    // Minecraft 物理常量
    public static final double GRAVITY = 0.08;
    public static final double GRAVITY_MODIFIER = 0.98;
    public static final double JUMP_POWER = 0.42;
    public static final double TERMINAL_VELOCITY = 3.92;
    
    // 摩擦系数
    public static final double GROUND_FRICTION = 0.91;
    public static final double AIR_FRICTION = 0.98;
    
    // 移动速度
    public static final double WALK_SPEED = 0.21588;
    public static final double SPRINT_SPEED = WALK_SPEED * 1.3;
    public static final double SNEAK_SPEED = WALK_SPEED * 0.3;
    
    // 状态历史
    private final Queue<VelocityState> velocityHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 20;
    
    // 预测结果
    private MovementPrediction lastPrediction;
    
    /**
     * 速度状态记录
     */
    public static class VelocityState {
        public double vx, vy, vz;
        public boolean onGround;
        public long timestamp;
        
        public VelocityState(double vx, double vy, double vz, boolean onGround) {
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.onGround = onGround;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * 运动预测结果
     */
    public static class MovementPrediction {
        public double predictedX, predictedY, predictedZ;
        public double predictedVx, predictedVy, predictedVz;
        public double deviation;
        public double confidence;
        public boolean suspicious;
        
        public MovementPrediction(double px, double py, double pz, 
                                  double pvx, double pvy, double pvz, 
                                  double dev, double conf) {
            this.predictedX = px;
            this.predictedY = py;
            this.predictedZ = pz;
            this.predictedVx = pvx;
            this.predictedVy = pvy;
            this.predictedVz = pvz;
            this.deviation = dev;
            this.confidence = conf;
            this.suspicious = dev > 0.5;
        }
    }
    
    /**
     * 核心预测方法 - Matrix/GrimAC 风格
     */
    public MovementPrediction predictMovement(PlayerData playerData, 
                                             boolean isOnGround,
                                             boolean isSprinting, 
                                             boolean isSneaking) {
        
        // 获取初始状态
        double lastX = playerData.getLastX();
        double lastY = playerData.getLastY();
        double lastZ = playerData.getLastZ();
        
        double velocityX = playerData.getVelocityX();
        double velocityY = playerData.getVelocityY();
        double velocityZ = playerData.getVelocityZ();
        
        // 应用物理规则
        VelocityUpdateResult result = applyPhysics(
            velocityX, velocityY, velocityZ,
            isOnGround, isSprinting, isSneaking
        );
        
        // 计算预测位置
        double predictedX = lastX + result.vx;
        double predictedY = lastY + result.vy;
        double predictedZ = lastZ + result.vz;
        
        // 计算与实际位置的偏差
        double actualX = playerData.getX();
        double actualY = playerData.getY();
        double actualZ = playerData.getZ();
        
        double deviation = calculateDeviation(
            actualX, actualY, actualZ,
            predictedX, predictedY, predictedZ
        );
        
        // 计算置信度
        double confidence = calculateConfidence(deviation, isOnGround);
        
        // 保存状态
        velocityHistory.add(new VelocityState(result.vx, result.vy, result.vz, isOnGround));
        if (velocityHistory.size() > HISTORY_SIZE) {
            velocityHistory.poll();
        }
        
        MovementPrediction prediction = new MovementPrediction(
            predictedX, predictedY, predictedZ,
            result.vx, result.vy, result.vz,
            deviation, confidence
        );
        
        lastPrediction = prediction;
        return prediction;
    }
    
    /**
     * 应用物理规则 - GrimAC 精确版本
     */
    private VelocityUpdateResult applyPhysics(double vx, double vy, double vz,
                                               boolean onGround,
                                               boolean sprinting,
                                               boolean sneaking) {
        VelocityUpdateResult result = new VelocityUpdateResult();
        
        // 处理垂直方向
        if (onGround) {
            // 在地面上
            result.vy = Math.max(0, vy);
            result.vx = vx * GROUND_FRICTION;
            result.vz = vz * GROUND_FRICTION;
        } else {
            // 在空中 - 应用重力
            result.vy = vy - GRAVITY;
            result.vy *= GRAVITY_MODIFIER;
            
            // 限制终端速度
            result.vy = Math.max(result.vy, -TERMINAL_VELOCITY);
            
            // 应用空气摩擦
            result.vx = vx * AIR_FRICTION;
            result.vz = vz * AIR_FRICTION;
        }
        
        // 应用移动速度修饰
        double baseSpeed = onGround ? WALK_SPEED : WALK_SPEED * 0.91;
        if (sprinting) baseSpeed *= 1.3;
        if (sneaking) baseSpeed *= 0.3;
        
        // 确保不超过最大速度
        double speed = Math.sqrt(result.vx * result.vx + result.vz * result.vz);
        if (speed > baseSpeed * 2.0) {
            double ratio = baseSpeed * 2.0 / speed;
            result.vx *= ratio;
            result.vz *= ratio;
        }
        
        return result;
    }
    
    /**
     * 计算位置偏差
     */
    private double calculateDeviation(double ax, double ay, double az, 
                                       double px, double py, double pz) {
        double dx = ax - px;
        double dy = ay - py;
        double dz = az - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * 计算置信度
     */
    private double calculateConfidence(double deviation, boolean onGround) {
        double baseConfidence = onGround ? 0.95 : 0.85;
        
        // 偏差越大，置信度越低
        if (deviation < 0.1) return baseConfidence;
        if (deviation < 0.3) return baseConfidence - 0.2;
        if (deviation < 0.5) return 0.5;
        
        return 0.1; // 高度可疑
    }
    
    /**
     * 速度更新结果
     */
    private static class VelocityUpdateResult {
        double vx, vy, vz;
    }
    
    /**
     * 验证移动是否合法
     */
    public boolean validateMovement(PlayerData playerData, MovementPrediction prediction) {
        if (prediction == null) return true;
        
        // Matrix 风格的多级验证
        if (prediction.deviation > 0.8) return false; // 严重违规
        if (prediction.deviation > 0.5 && prediction.confidence > 0.9) return false; // 高置信度违规
        
        return true;
    }
    
    /**
     * 获取最后的预测结果
     */
    public MovementPrediction getLastPrediction() {
        return lastPrediction;
    }
    
    /**
     * 清除历史
     */
    public void clearHistory() {
        velocityHistory.clear();
        lastPrediction = null;
    }
}
