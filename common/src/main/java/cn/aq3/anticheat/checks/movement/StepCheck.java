package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class StepCheck extends Check {
    
    public StepCheck() {
        super("Step", "检测台阶作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.movement.step.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.25;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        if (!playerData.isOnGround() || playerData.wasOnGround()) {
            return false;
        }
        
        double verticalDistance = playerData.getY() - playerData.getLastY();
        
        double maxStepHeight = 0.6 * thresholdMultiplier;
        
        if (verticalDistance > maxStepHeight && verticalDistance < 2.0) {
            if (!playerData.wasOnGround() && !playerData.isOnGround()) {
                return true;
            }
        }
        
        return false;
    }
}
