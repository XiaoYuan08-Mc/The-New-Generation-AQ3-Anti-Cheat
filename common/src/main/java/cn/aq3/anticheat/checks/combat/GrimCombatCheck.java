package cn.aq3.anticheat.checks.combat;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GrimAC风格的综合战斗检测
 * 检测各种战斗相关的作弊
 */
public class GrimCombatCheck extends Check {
    
    // 攻击数据记录
    private final Map<UUID, AttackData> attackDataMap = new HashMap<>();
    
    // 正常游戏的最大CPS
    private static final int MAX_CPS = 20;
    
    // 正常攻击距离
    private static final double MAX_REACH = 3.5;
    
    public GrimCombatCheck() {
        super("GrimCombat", "GrimAC综合战斗检测", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        if (playerData == null) {
            return false;
        }
        
        UUID playerUUID = playerData.getUuid();
        AttackData attackData = attackDataMap.computeIfAbsent(playerUUID, k -> new AttackData());
        
        // 检查AutoClicker（CPS过高）
        if (checkAutoClicker(attackData)) {
            return true;
        }
        
        // 检查屠杀光环（同时攻击多个目标）
        if (checkKillAura(attackData, playerData)) {
            return true;
        }
        
        // 检查Reaches（攻击距离过远）
        if (checkReach(attackData)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 检查自动点击器
     */
    private boolean checkAutoClicker(AttackData attackData) {
        // 清理过期的攻击时间
        attackData.cleanupOldAttacks(System.currentTimeMillis());
        
        // 获取过去1秒内的攻击次数
        int attacksLastSecond = attackData.getAttackCountLastSecond();
        
        // 如果CPS超过正常值，可能是AutoClicker
        if (attacksLastSecond > MAX_CPS) {
            attackData.incrementAutoClickerFlag();
            if (attackData.getAutoClickerFlags() >= 3) {
                return true;
            }
        } else {
            attackData.decrementAutoClickerFlags();
        }
        
        return false;
    }
    
    /**
     * 检查屠杀光环
     */
    private boolean checkKillAura(AttackData attackData, PlayerData playerData) {
        // 获取最近攻击的不同实体数量
        int uniqueEntities = attackData.getUniqueEntityCount();
        
        // 如果在短时间内攻击了多个不同的实体，可能是屠杀光环
        if (uniqueEntities > 3) {
            attackData.incrementKillAuraFlag();
            if (attackData.getKillAuraFlags() >= 3) {
                return true;
            }
        } else {
            attackData.decrementKillAuraFlags();
        }
        
        return false;
    }
    
    /**
     * 检查攻击距离
     */
    private boolean checkReach(AttackData attackData) {
        double lastReach = attackData.getLastReach();
        
        if (lastReach > MAX_REACH) {
            attackData.incrementReachFlag();
            if (attackData.getReachFlags() >= 3) {
                return true;
            }
        } else {
            attackData.decrementReachFlags();
        }
        
        return false;
    }
    
    /**
     * 记录攻击
     */
    public void recordAttack(UUID playerUUID, long entityId, double reach) {
        AttackData attackData = attackDataMap.computeIfAbsent(playerUUID, k -> new AttackData());
        attackData.recordAttack(System.currentTimeMillis(), entityId, reach);
    }
    
    /**
     * 获取玩家的攻击数据
     */
    public AttackData getAttackData(UUID playerUUID) {
        return attackDataMap.get(playerUUID);
    }
    
    /**
     * 清理玩家的攻击数据
     */
    public void clearAttackData(UUID playerUUID) {
        attackDataMap.remove(playerUUID);
    }
    
    /**
     * 攻击数据结构
     */
    public static class AttackData {
        private final Map<Long, Long> attackTimestamps = new HashMap<>();
        private final Map<Long, Boolean> attackedEntities = new HashMap<>();
        private double lastReach = 0;
        
        private int autoClickerFlags = 0;
        private int killAuraFlags = 0;
        private int reachFlags = 0;
        
        public void recordAttack(long timestamp, long entityId, double reach) {
            attackTimestamps.put(timestamp, entityId);
            attackedEntities.put(entityId, true);
            this.lastReach = Math.max(this.lastReach, reach);
        }
        
        public void cleanupOldAttacks(long currentTime) {
            long oneSecondAgo = currentTime - 1000;
            attackTimestamps.entrySet().removeIf(entry -> entry.getKey() < oneSecondAgo);
        }
        
        public int getAttackCountLastSecond() {
            return attackTimestamps.size();
        }
        
        public int getUniqueEntityCount() {
            return attackedEntities.size();
        }
        
        public double getLastReach() {
            return lastReach;
        }
        
        public void incrementAutoClickerFlag() {
            autoClickerFlags++;
        }
        
        public void decrementAutoClickerFlags() {
            autoClickerFlags = Math.max(0, autoClickerFlags - 1);
        }
        
        public int getAutoClickerFlags() {
            return autoClickerFlags;
        }
        
        public void incrementKillAuraFlag() {
            killAuraFlags++;
        }
        
        public void decrementKillAuraFlags() {
            killAuraFlags = Math.max(0, killAuraFlags - 1);
        }
        
        public int getKillAuraFlags() {
            return killAuraFlags;
        }
        
        public void incrementReachFlag() {
            reachFlags++;
        }
        
        public void decrementReachFlags() {
            reachFlags = Math.max(0, reachFlags - 1);
        }
        
        public int getReachFlags() {
            return reachFlags;
        }
        
        public void reset() {
            attackTimestamps.clear();
            attackedEntities.clear();
            lastReach = 0;
            autoClickerFlags = 0;
            killAuraFlags = 0;
            reachFlags = 0;
        }
    }
}
