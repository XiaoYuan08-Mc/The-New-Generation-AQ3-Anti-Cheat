package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class ElytraCheck extends Check {
    
    public ElytraCheck() {
        super("Elytra", "检测鞘翅飞行作弊", true, 15);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.movement.elytra.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 10000) {
            return false;
        }
        
        if (!playerData.isFlyingWithElytra()) {
            return false;
        }
        
        double horizontalSpeed = Math.sqrt(
            Math.pow(playerData.getX() - playerData.getLastX(), 2) +
            Math.pow(playerData.getZ() - playerData.getLastZ(), 2)
        );
        
        double maxNormalSpeed = 0.15 * thresholdMultiplier;
        
        if (horizontalSpeed > maxNormalSpeed) {
            double excessSpeed = horizontalSpeed - maxNormalSpeed;
            if (excessSpeed > 0.05) {
                return true;
            }
        }
        
        double verticalSpeed = Math.abs(playerData.getY() - playerData.getLastY());
        
        if (verticalSpeed > 0.3 * thresholdMultiplier) {
            return true;
        }
        
        return false;
    }
}
