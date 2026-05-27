package cn.aq3.anticheat.checks.player;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

public class FoodCheck extends Check {
    
    public FoodCheck() {
        super("Food", "检测食物作弊", true, 8);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        boolean checkEnabled = AQ3API.getInstance().getConfigManager()
            .getBoolean("checks.player.food.enabled", true);
        if (!checkEnabled) {
            return false;
        }
        
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.25;
        
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) {
            return false;
        }
        
        int foodGain = playerData.getFoodLevel() - playerData.getLastFoodLevel();
        
        int maxNormalGain = 4;
        
        if (foodGain > maxNormalGain) {
            return true;
        }
        
        long timeSinceLastEat = System.currentTimeMillis() - playerData.getLastEatTime();
        
        if (timeSinceLastEat < 3000 && foodGain > 0) {
            int expectedGain = (int) Math.min(4, foodGain);
            if (foodGain > expectedGain * thresholdMultiplier) {
                return true;
            }
        }
        
        return false;
    }
}
