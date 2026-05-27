package cn.aq3.anticheat.checks;

import cn.aq3.anticheat.checks.combat.*;
import cn.aq3.anticheat.checks.inventory.FastUseCheck;
import cn.aq3.anticheat.checks.inventory.InventoryMoveCheck;
import cn.aq3.anticheat.checks.ml.PatternAnalysisCheck;
import cn.aq3.anticheat.checks.movement.*;
import cn.aq3.anticheat.checks.network.PingSpoofCheck;
import cn.aq3.anticheat.checks.player.*;
import cn.aq3.anticheat.checks.world.*;
import cn.aq3.anticheat.ml.CheatPatternDetector;
import cn.aq3.anticheat.player.PlayerData;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查管理器
 * Check manager
 */
public class CheckManager {
    private final List<Check> checks = new ArrayList<>();
    private final CheatPatternDetector patternDetector;
    
    public CheckManager() {
        this.patternDetector = new CheatPatternDetector();
        // 注册所有检查
        // Register all checks
        registerChecks();
    }
    
    /**
     * 注册所有检查
     * Register all checks
     */
    private void registerChecks() {
        checks.add(new SpeedCheck());
        checks.add(new FlyCheck());
        checks.add(new JesusCheck());
        checks.add(new KillAuraCheck());
        checks.add(new ReachCheck());
        checks.add(new FastUseCheck());
        checks.add(new TimerCheck());
        checks.add(new AutoClickerCheck());
        checks.add(new NoFallCheck());
        checks.add(new PhaseCheck());
        checks.add(new VelocityCheck());
        checks.add(new CriticalsCheck());
        checks.add(new ScaffoldCheck());
        checks.add(new FastBreakCheck());
        checks.add(new RegenCheck());
        checks.add(new InventoryMoveCheck());
        checks.add(new EntityInteractCheck());
        checks.add(new ChatCheck());
        checks.add(new NukerCheck());
        checks.add(new PingSpoofCheck());
        checks.add(new PatternAnalysisCheck(patternDetector));
        
        checks.add(new ElytraCheck());
        checks.add(new TeleportCheck());
        checks.add(new StepCheck());
        checks.add(new ExperienceCheck());
        checks.add(new FoodCheck());
        checks.add(new HealthCheck());
        checks.add(new BlockPlaceCheck());
        checks.add(new ExcavationCheck());
        checks.add(new FastDigCheck());
    }
    
    /**
     * 对玩家执行所有启用的检查
     * Perform all enabled checks on player
     */
    public List<Check> performChecks(PlayerData playerData) {
        List<Check> failedChecks = new ArrayList<>();
        
        for (Check check : checks) {
            if (check.isEnabled() && check.performCheck(playerData)) {
                failedChecks.add(check);
            }
        }
        
        return failedChecks;
    }
    
    /**
     * 获取所有检查
     * Get all checks
     */
    public List<Check> getChecks() {
        return new ArrayList<>(checks);
    }
    
    /**
     * 获取特定类型的检查
     * Get specific type of check
     */
    public Check getCheck(Class<? extends Check> checkClass) {
        for (Check check : checks) {
            if (checkClass.isInstance(check)) {
                return check;
            }
        }
        return null;
    }
}