package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class TeleportCheck extends Check {
    
    public TeleportCheck() {
        super("Teleport", "检测传送作弊", true, 5);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.movement.teleport.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.4;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        double distance = Math.sqrt(
            Math.pow(playerData.getX() - playerData.getLastX(), 2) +
            Math.pow(playerData.getY() - playerData.getLastY(), 2) +
            Math.pow(playerData.getZ() - playerData.getLastZ(), 2)
        );
        
        double maxNormalDistance = 10.0 * thresholdMultiplier;
        
        if (distance > maxNormalDistance) {
            long now = System.currentTimeMillis();
            if (playerData.getLastTeleportTime() > 0 && now - playerData.getLastTeleportTime() < 5000) {
                return false;
            }
            
            if (playerData.getLastEnderPearlTime() > 0 && now - playerData.getLastEnderPearlTime() < 1000) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }
}
