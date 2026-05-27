package cn.aq3.anticheat.state;

import cn.aq3.anticheat.player.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Matrix 风格的状态验证系统
 * Matrix-style State Validation System
 * 
 * 核心功能：
 * - 状态连续性验证
 * - 跳跃检测
 * - 地面状态验证
 * - 模式分析
 */
public class StateValidator {
    
    // 状态历史
    private final Queue<PlayerState> stateHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 25;
    
    // 违规计数
    private int consecutiveGroundFailures = 0;
    private int consecutiveJumpFailures = 0;
    
    /**
     * 玩家状态记录
     */
    public static class PlayerState {
        public double x, y, z;
        public double vx, vy, vz;
        public boolean onGround;
        public boolean sneaking;
        public boolean sprinting;
        public long timestamp;
        
        public PlayerState(double x, double y, double z,
                           double vx, double vy, double vz,
                           boolean onGround, boolean sneaking, boolean sprinting) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.onGround = onGround;
            this.sneaking = sneaking;
            this.sprinting = sprinting;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * 验证结果
     */
    public static class ValidationResult {
        public boolean valid;
        public String reason;
        public double confidence;
        public int violationLevel;
        
        public ValidationResult(boolean valid, String reason, double confidence, int level) {
            this.valid = valid;
            this.reason = reason;
            this.confidence = confidence;
            this.violationLevel = level;
        }
    }
    
    /**
     * 添加状态并进行验证
     */
    public ValidationResult addStateAndValidate(PlayerData playerData) {
        PlayerState state = createState(playerData);
        stateHistory.add(state);
        
        // 保持历史大小
        while (stateHistory.size() > HISTORY_SIZE) {
            stateHistory.poll();
        }
        
        // 综合验证
        return validateState(state);
    }
    
    /**
     * 创建状态对象
     */
    private PlayerState createState(PlayerData playerData) {
        return new PlayerState(
            playerData.getX(), playerData.getY(), playerData.getZ(),
            playerData.getVelocityX(), playerData.getVelocityY(), playerData.getVelocityZ(),
            playerData.isOnGround(), playerData.isSneaking(), playerData.isSprinting()
        );
    }
    
    /**
     * 综合验证状态
     */
    public ValidationResult validateState(PlayerState state) {
        // 验证 1: 地面状态一致性
        ValidationResult groundCheck = validateGroundState(state);
        if (!groundCheck.valid) return groundCheck;
        
        // 验证 2: 跳跃检测
        ValidationResult jumpCheck = validateJump(state);
        if (!jumpCheck.valid) return jumpCheck;
        
        // 验证 3: 速度合理性
        ValidationResult speedCheck = validateSpeed(state);
        if (!speedCheck.valid) return speedCheck;
        
        // 全部通过
        return new ValidationResult(true, "All checks passed", 1.0, 0);
    }
    
    /**
     * 验证地面状态 - Matrix 风格
     */
    private ValidationResult validateGroundState(PlayerState state) {
        if (stateHistory.size() < 2) {
            return new ValidationResult(true, "Not enough history", 0.5, 0);
        }
        
        // 获取上一个状态
        PlayerState lastState = stateHistory.toArray(new PlayerState[0])[stateHistory.size() - 2];
        
        // 检测 ground 状态突变
        if (lastState.onGround && !state.onGround) {
            // 从地面到空中 - 应该是跳跃
            double expectedJumpVelocity = 0.42;
            if (Math.abs(state.vy - expectedJumpVelocity) > 0.2) {
                consecutiveGroundFailures++;
                if (consecutiveGroundFailures >= 3) {
                    return new ValidationResult(
                        false, 
                        "Abnormal jump velocity: " + state.vy, 
                        0.9, 
                        2
                    );
                }
            } else {
                consecutiveGroundFailures = 0;
            }
        }
        
        // 在空中但地面状态为 true
        if (state.onGround && state.vy > 0.1) {
            consecutiveGroundFailures++;
            if (consecutiveGroundFailures >= 2) {
                return new ValidationResult(
                    false,
                    "Invalid onGround state while moving up",
                    0.85,
                    1
                );
            }
        } else {
            consecutiveGroundFailures = 0;
        }
        
        return new ValidationResult(true, "Ground state valid", 0.9, 0);
    }
    
    /**
     * 验证跳跃 - GrimAC 风格
     */
    private ValidationResult validateJump(PlayerState state) {
        if (stateHistory.size() < 3) {
            return new ValidationResult(true, "Not enough history", 0.5, 0);
        }
        
        PlayerState[] states = stateHistory.toArray(new PlayerState[0]);
        PlayerState prevState = states[states.length - 2];
        PlayerState prevPrevState = states[states.length - 3];
        
        // 检测跳跃模式
        if (!prevState.onGround && state.onGround) {
            // 刚刚着陆
            double deltaY = state.y - prevState.y;
            if (deltaY > 0.5) {
                consecutiveJumpFailures++;
                if (consecutiveJumpFailures >= 3) {
                    return new ValidationResult(
                        false,
                        "Abnormal landing delta: " + deltaY,
                        0.88,
                        2
                    );
                }
            } else {
                consecutiveJumpFailures = 0;
            }
        }
        
        // 检测空中突然升起
        if (!prevPrevState.onGround && !prevState.onGround && !state.onGround) {
            if (state.vy > 0.3 && prevState.vy < 0) {
                // 从下降突然变成快速上升 - 可疑
                return new ValidationResult(
                    false,
                    "Abnormal upward velocity change",
                    0.82,
                    1
                );
            }
        }
        
        return new ValidationResult(true, "Jump validation passed", 0.92, 0);
    }
    
    /**
     * 验证速度合理性
     */
    private ValidationResult validateSpeed(PlayerState state) {
        double horizontalSpeed = Math.sqrt(state.vx * state.vx + state.vz * state.vz);
        
        // 正常移动速度限制
        double maxSpeed = state.sprinting ? 0.32 : 0.28;
        if (state.sneaking) maxSpeed = 0.12;
        
        if (horizontalSpeed > maxSpeed * 2.0) {
            return new ValidationResult(
                false,
                "Excessive horizontal speed: " + horizontalSpeed,
                0.95,
                3
            );
        }
        
        // 垂直速度限制
        if (state.vy > 2.0) {
            return new ValidationResult(
                false,
                "Excessive upward velocity: " + state.vy,
                0.9,
                2
            );
        }
        
        return new ValidationResult(true, "Speed validation passed", 0.9, 0);
    }
    
    /**
     * 重置违规计数
     */
    public void resetViolations() {
        consecutiveGroundFailures = 0;
        consecutiveJumpFailures = 0;
    }
    
    /**
     * 清除历史
     */
    public void clearHistory() {
        stateHistory.clear();
        resetViolations();
    }
}
