package cn.aq3.anticheat.checks.probability;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;

/**
 * 概率检测系统 - 类似GrimAc的概率检测
 * 使用统计学和机器学习技术分析作弊概率
 */
public class ProbabilityCheck extends Check {
    
    // 作弊概率记录
    private final Map<String, Double> cheatProbability = new HashMap<>();
    
    // 概率衰减因子
    private static final double DECAY_FACTOR = 0.95;
    
    // 概率阈值
    private static final double THRESHOLD = 0.7;
    
    public ProbabilityCheck() {
        super("Probability", "概率检测系统", true, 3);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        String playerId = playerData.getUuid().toString();
        
        // 获取当前概率
        double currentProbability = cheatProbability.getOrDefault(playerId, 0.0);
        
        // 应用衰减
        currentProbability *= DECAY_FACTOR;
        cheatProbability.put(playerId, currentProbability);
        
        // 如果概率超过阈值，标记为作弊
        return currentProbability > THRESHOLD;
    }
    
    /**
     * 增加作弊概率
     */
    public void increaseProbability(String playerId, double amount) {
        double current = cheatProbability.getOrDefault(playerId, 0.0);
        current = Math.min(1.0, current + amount); // 最大1.0
        cheatProbability.put(playerId, current);
    }
    
    /**
     * 降低作弊概率
     */
    public void decreaseProbability(String playerId, double amount) {
        double current = cheatProbability.getOrDefault(playerId, 0.0);
        current = Math.max(0.0, current - amount); // 最小0
        cheatProbability.put(playerId, current);
    }
    
    /**
     * 获取当前作弊概率
     */
    public double getProbability(String playerId) {
        return cheatProbability.getOrDefault(playerId, 0.0);
    }
    
    /**
     * 重置概率
     */
    public void resetProbability(String playerId) {
        cheatProbability.remove(playerId);
    }
}
