package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class ExcavationCheck extends Check {
    
    public ExcavationCheck() {
        super("Excavation", "检测挖掘作弊", true, 12);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.world.excavation.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.25;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        int blockBreakCount = playerData.getBlockBreakCount();
        long checkPeriod = 1000;
        
        if (blockBreakCount > 20 * thresholdMultiplier) {
            return true;
        }
        
        long now = System.currentTimeMillis();
        if (playerData.getLastBreakCheckTime() > 0 && now - playerData.getLastBreakCheckTime() >= checkPeriod) {
            playerData.setBlockBreakCount(0);
            playerData.setLastBreakCheckTime(now);
        }
        
        return false;
    }
}
