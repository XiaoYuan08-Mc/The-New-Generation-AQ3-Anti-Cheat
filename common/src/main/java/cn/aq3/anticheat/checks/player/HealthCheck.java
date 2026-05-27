package cn.aq3.anticheat.checks.player;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class HealthCheck extends Check {
    
    public HealthCheck() {
        super("Health", "检测生命值作弊", true, 8);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.player.health.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 10000) {
            return false;
        }
        
        double healthGain = playerData.getHealth() - playerData.getLastHealth();
        
        double maxNormalGain = 4.0 * thresholdMultiplier;
        
        if (healthGain > maxNormalGain) {
            long timeSinceLastHeal = System.currentTimeMillis() - playerData.getLastHealTime();
            
            if (timeSinceLastHeal > 5000) {
                return true;
            }
        }
        
        return false;
    }
}
