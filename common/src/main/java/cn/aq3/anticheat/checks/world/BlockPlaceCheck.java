package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class BlockPlaceCheck extends Check {
    
    public BlockPlaceCheck() {
        super("BlockPlace", "检测方块放置作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.world.block_place.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        int placeCount = playerData.getBlockPlaceCount();
        long checkPeriod = 5000;
        long now = System.currentTimeMillis();
        
        if (playerData.getLastPlaceCheckTime() > 0 && now - playerData.getLastPlaceCheckTime() >= checkPeriod) {
            playerData.setBlockPlaceCount(0);
            playerData.setLastPlaceCheckTime(now);
        }
        
        if (placeCount > 50 * thresholdMultiplier) {
            return true;
        }
        
        return false;
    }
}
