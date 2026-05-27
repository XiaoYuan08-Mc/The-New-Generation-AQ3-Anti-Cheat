package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class FastDigCheck extends Check {
    
    public FastDigCheck() {
        super("FastDig", "检测快速挖掘作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.world.fast_dig.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.3;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        long digTime = playerData.getLastDigTime();
        
        if (digTime > 0) {
            double minDigTime = 100.0 / thresholdMultiplier;
            
            if (digTime < minDigTime) {
                return true;
            }
        }
        
        return false;
    }
}
