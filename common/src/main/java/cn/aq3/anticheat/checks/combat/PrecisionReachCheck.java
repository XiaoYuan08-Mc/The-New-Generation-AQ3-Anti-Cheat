package cn.aq3.anticheat.checks.combat;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 精确距离检测 - 类似GrimAc的精确距离检测
 * 精确计算玩家与目标实体之间的距离，检测超出合理范围的攻击
 */
public class PrecisionReachCheck extends Check {
    
    // 历史攻击距离记录
    private final Queue<Double> reachHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 20;
    
    // 正常游戏的最大攻击距离 (方块)
    private static final double NORMAL_MAX_REACH = 3.0;
    
    // 3D距离计算容差
    private static final double TOLERANCE = 0.05;
    
    public PrecisionReachCheck() {
        super("PrecisionReach", "精确攻击距离检测", true, 8);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        if (playerData == null) {
            return false;
        }
        
        // 获取最近攻击记录
        Long lastAttackTime = playerData.getLastAttackTime();
        if (lastAttackTime == null) {
            return false;
        }
        
        // 如果在一段时间内没有攻击，不检测
        long timeSinceAttack = System.currentTimeMillis() - lastAttackTime;
        if (timeSinceAttack > 1000) {
            return false;
        }
        
        return false; // 需要结合Bukkit事件获取实体位置
    }
    
    /**
     * 记录攻击距离（从Bukkit事件调用）
     */
    public void recordAttack(double distance) {
        reachHistory.offer(distance);
        
        // 保持历史大小
        while (reachHistory.size() > HISTORY_SIZE) {
            reachHistory.poll();
        }
    }
    
    /**
     * 精确3D距离计算
     */
    public static double calculate3DDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * 获取平均攻击距离
     */
    public double getAverageReach() {
        if (reachHistory.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double reach : reachHistory) {
            sum += reach;
        }
        return sum / reachHistory.size();
    }
    
    /**
     * 获取最大攻击距离
     */
    public double getMaxReach() {
        double max = 0;
        for (double reach : reachHistory) {
            if (reach > max) {
                max = reach;
            }
        }
        return max;
    }
}
