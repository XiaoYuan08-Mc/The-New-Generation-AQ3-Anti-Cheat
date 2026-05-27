package cn.aq3.anticheat.physics;

import cn.aq3.anticheat.player.PlayerData;

/**
 * GrimAc风格的运动预测引擎
 * 预测玩家的下一步位置，验证是否符合物理规则
 */
public class PredictionEngine {
    
    // 重力加速度 (方块/秒²)
    private static final double GRAVITY = -0.08;
    
    // 空气阻力
    private static final double AIR_RESISTANCE = 0.98;
    
    // 摩擦力
    private static final double FRICTION = 0.91;
    
    // 跳跃初始速度
    private static final double JUMP_VELOCITY = 0.42;
    
    // 最大垂直速度
    private static final double MAX_VERTICAL_VELOCITY = 3.92;
    
    // 最大水平速度
    private static final double MAX_HORIZONTAL_VELOCITY = 0.2873;
    
    /**
     * 预测玩家下一步位置
     */
    public static PredictionResult predictNextPosition(PlayerData playerData, boolean isOnGround, boolean isSprinting, boolean isSneaking) {
        PredictionResult result = new PredictionResult();
        
        // 当前速度
        double velX = playerData.getVelocityX();
        double velY = playerData.getVelocityY();
        double velZ = playerData.getVelocityZ();
        
        // 当前位置
        double lastX = playerData.getLastX();
        double lastY = playerData.getLastY();
        double lastZ = playerData.getLastZ();
        
        // 应用物理规则
        if (isOnGround) {
            // 地面移动 - 应用摩擦力
            velX *= FRICTION;
            velZ *= FRICTION;
            
            // 降低垂直速度
            velY = Math.max(velY, 0);
        } else {
            // 在空中 - 应用重力
            velY += GRAVITY;
            
            // 限制最大坠落速度
            velY = Math.max(velY, -MAX_VERTICAL_VELOCITY);
            
            // 应用空气阻力
            velX *= AIR_RESISTANCE;
            velZ *= AIR_RESISTANCE;
        }
        
        // 计算预测位置
        double predictedX = lastX + velX;
        double predictedY = lastY + velY;
        double predictedZ = lastZ + velZ;
        
        // 设置结果
        result.predictedX = predictedX;
        result.predictedY = predictedY;
        result.predictedZ = predictedZ;
        result.predictedVelX = velX;
        result.predictedVelY = velY;
        result.predictedVelZ = velZ;
        
        return result;
    }
    
    /**
     * 验证移动是否符合预测
     */
    public static boolean validateMovement(PlayerData playerData, double actualX, double actualY, double actualZ, PredictionResult prediction) {
        // 计算偏差
        double deltaX = Math.abs(actualX - prediction.predictedX);
        double deltaY = Math.abs(actualY - prediction.predictedY);
        double deltaZ = Math.abs(actualZ - prediction.predictedZ);
        
        // 容差范围
        double toleranceXZ = 0.1; // 水平容差
        double toleranceY = 0.15; // 垂直容差稍大
        
        // 检查是否在容差范围内
        return deltaX <= toleranceXZ && deltaY <= toleranceY && deltaZ <= toleranceXZ;
    }
    
    /**
     * 计算位置偏差
     */
    public static double calculateDeviation(PlayerData playerData, double actualX, double actualY, double actualZ, PredictionResult prediction) {
        double deltaX = actualX - prediction.predictedX;
        double deltaY = actualY - prediction.predictedY;
        double deltaZ = actualZ - prediction.predictedZ;
        
        // 计算欧几里得距离
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    /**
     * 预测结果类
     */
    public static class PredictionResult {
        public double predictedX;
        public double predictedY;
        public double predictedZ;
        public double predictedVelX;
        public double predictedVelY;
        public double predictedVelZ;
    }
}
