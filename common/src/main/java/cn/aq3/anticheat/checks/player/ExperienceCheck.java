package cn.aq3.anticheat.checks.player;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class ExperienceCheck extends Check {
    
    public ExperienceCheck() {
        super("Experience", "检测经验作弊", true, 8);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.player.experience.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 10000) {
            return false;
        }
        
        double expGain = playerData.getExperience() - playerData.getLastExperience();
        
        double maxNormalGain = 50.0 * thresholdMultiplier;
        
        if (expGain > maxNormalGain) {
            return true;
        }
        
        return false;
    }
}
