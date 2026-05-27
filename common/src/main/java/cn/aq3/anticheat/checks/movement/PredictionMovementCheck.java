package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.physics.PredictionEngine;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 预测运动检测 - 类似GrimAc的精确运动检测
 * 使用预测引擎验证玩家移动是否符合物理规则
 */
public class PredictionMovementCheck extends Check {
    
    // 最大容差
    private static final double MAX_DEVIATION = 0.3;
    
    // 连续异常计数
    private int consecutiveViolations = 0;
    private static final int MAX_CONSECUTIVE = 3;
    
    public PredictionMovementCheck() {
        super("PredictionMovement", "预测运动检测", true, 5);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        if (playerData == null) {
            return false;
        }
        
        // 检查加入时间
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        // 获取玩家状态
        boolean isOnGround = playerData.isOnGround();
        boolean isSprinting = playerData.isSprinting();
        boolean isSneaking = playerData.isSneaking();
        
        // 预测下一步位置
        PredictionEngine.PredictionResult prediction = 
            PredictionEngine.predictNextPosition(playerData, isOnGround, isSprinting, isSneaking);
        
        // 获取实际位置
        double actualX = playerData.getX();
        double actualY = playerData.getY();
        double actualZ = playerData.getZ();
        
        // 计算偏差
        double deviation = PredictionEngine.calculateDeviation(
            playerData, actualX, actualY, actualZ, prediction);
        
        // 检查偏差是否过大
        if (deviation > MAX_DEVIATION) {
            consecutiveViolations++;
            
            // 连续异常次数过多
            if (consecutiveViolations >= MAX_CONSECUTIVE) {
                consecutiveViolations = 0;
                return true;
            }
        } else {
            // 正常移动，重置计数
            consecutiveViolations = Math.max(0, consecutiveViolations - 1);
        }
        
        return false;
    }
}
