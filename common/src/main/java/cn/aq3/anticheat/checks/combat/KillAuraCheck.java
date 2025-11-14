package cn.aq3.anticheat.checks.combat;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 杀戮光环检查
 * KillAura check
 */
public class KillAuraCheck extends Check {
    
    public KillAuraCheck() {
        super("KillAura", "检测杀戮光环作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        // 检查是否启用（动态读取配置）
        // Check if enabled (dynamically read configuration)
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.combat.killaura.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        // 获取最大违规次数（动态读取配置）
        // Get maximum violations (dynamically read configuration)
        int maxViolations = AQ3API.getInstance().getConfigManager()
            .getInt("checks.combat.killaura.max_violations", 10);
        
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 检查玩家是否刚刚加入游戏，如果是则跳过检查
        // Check if player just joined the game, skip check if so
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) { // 5秒内加入游戏的玩家不检查 / Don't check players who joined in the last 5 seconds
            return false;
        }
        
        // 检查视角变化是否异常（简化实现）
        // Check if look changes are abnormal (simplified implementation)
        float yawDelta = Math.abs(playerData.getYaw() - playerData.getLastYaw());
        float pitchDelta = Math.abs(playerData.getPitch() - playerData.getLastPitch());
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        
        // 如果视角变化过于频繁或剧烈，则可能是KillAura
        // If look changes are too frequent or drastic, may be KillAura
        return yawDelta > 100 * thresholdMultiplier || pitchDelta > 50 * thresholdMultiplier;
    }
}