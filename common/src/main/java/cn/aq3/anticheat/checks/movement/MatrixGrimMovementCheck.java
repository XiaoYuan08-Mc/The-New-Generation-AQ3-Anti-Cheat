package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;
import cn.aq3.anticheat.physics.GrimACMovementEngine;
import cn.aq3.anticheat.state.StateValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Matrix + GrimAC 风格的综合运动检测
 * Matrix + GrimAC-style Comprehensive Movement Check
 * 
 * 核心功能：
 * - 精确的物理预测 (GrimAC)
 * - 严格的状态验证 (Matrix)
 * - 多层置信度评估
 */
public class MatrixGrimMovementCheck extends Check {
    
    // 玩家引擎实例
    private final Map<UUID, GrimACMovementEngine> movementEngines = new HashMap<>();
    private final Map<UUID, StateValidator> stateValidators = new HashMap<>();
    
    // 违规计数
    private final Map<UUID, Integer> violationLevels = new HashMap<>();
    
    // 阈值
    private static final int MIN_VIOLATIONS_TO_FLAG = 5;
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.85;
    private static final double CRITICAL_DEVIATION = 0.8;
    
    public MatrixGrimMovementCheck() {
        super("MatrixGrimMove", "Matrix+GrimAC Movement Check", true, 1);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) return false;
        if (playerData == null) return false;
        
        UUID uuid = playerData.getUuid();
        
        // 获取或创建引擎
        GrimACMovementEngine engine = movementEngines.computeIfAbsent(
            uuid, k -> new GrimACMovementEngine()
        );
        
        StateValidator validator = stateValidators.computeIfAbsent(
            uuid, k -> new StateValidator()
        );
        
        // 阶段 1: GrimAC 物理预测
        GrimACMovementEngine.MovementPrediction prediction = engine.predictMovement(
            playerData,
            playerData.isOnGround(),
            playerData.isSprinting(),
            playerData.isSneaking()
        );
        
        // 阶段 2: Matrix 状态验证
        StateValidator.ValidationResult stateResult = validator.addStateAndValidate(playerData);
        
        // 阶段 3: 综合评估
        return evaluate(playerData, uuid, prediction, stateResult);
    }
    
    /**
     * 综合评估
     */
    private boolean evaluate(PlayerData playerData, UUID uuid,
                            GrimACMovementEngine.MovementPrediction prediction,
                            StateValidator.ValidationResult stateResult) {
        
        int violations = violationLevels.getOrDefault(uuid, 0);
        boolean shouldFlag = false;
        
        // 评估 GrimAC 预测结果
        if (prediction.suspicious && prediction.confidence > HIGH_CONFIDENCE_THRESHOLD) {
            violations += 2; // 高置信度违规权重更高
        } else if (prediction.deviation > 0.3) {
            violations++;
        }
        
        // 评估 Matrix 状态验证
        if (!stateResult.valid) {
            violations += stateResult.violationLevel;
        }
        
        // 严重违规快速检测
        if (prediction.deviation > CRITICAL_DEVIATION) {
            violations += 3;
        }
        
        // 更新违规水平
        violationLevels.put(uuid, violations);
        
        // 衰减机制 - 自然减少违规计数
        if (violations > 0) {
            violationLevels.put(uuid, Math.max(0, violations - 1));
        }
        
        // 判定
        if (violations >= MIN_VIOLATIONS_TO_FLAG) {
            shouldFlag = true;
            // 重置违规计数，避免持续触发
            violationLevels.put(uuid, MIN_VIOLATIONS_TO_FLAG - 2);
        }
        
        return shouldFlag;
    }
    
    /**
     * 获取违规级别
     */
    public int getViolationLevel(UUID uuid) {
        return violationLevels.getOrDefault(uuid, 0);
    }
    
    /**
     * 重置玩家状态
     */
    public void resetPlayer(UUID uuid) {
        violationLevels.remove(uuid);
        
        GrimACMovementEngine engine = movementEngines.get(uuid);
        if (engine != null) engine.clearHistory();
        
        StateValidator validator = stateValidators.get(uuid);
        if (validator != null) validator.clearHistory();
    }
    
    /**
     * 清除所有
     */
    public void clearAll() {
        movementEngines.clear();
        stateValidators.clear();
        violationLevels.clear();
    }
}
