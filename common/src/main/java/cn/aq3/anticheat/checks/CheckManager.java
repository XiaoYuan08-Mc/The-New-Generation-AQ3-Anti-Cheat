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
        checks.add(new NoFallCheck()); // 添加无摔落伤害检查 / Add no fall check
        checks.add(new PhaseCheck()); // 添加相位检查 / Add phase check
        checks.add(new VelocityCheck()); // 添加速度检查 / Add velocity check
        checks.add(new CriticalsCheck()); // 添加暴击检查 / Add criticals check
        checks.add(new ScaffoldCheck()); // 添加脚手架检查 / Add scaffold check
        checks.add(new FastBreakCheck()); // 添加快速破坏检查 / Add fast break check
        checks.add(new RegenCheck()); // 添加生命值恢复检查 / Add regen check
        checks.add(new InventoryMoveCheck()); // 添加背包移动检查 / Add inventory move check
        checks.add(new EntityInteractCheck()); // 添加实体交互检查 / Add entity interact check
        checks.add(new ChatCheck()); // 添加聊天检查 / Add chat check
        checks.add(new NukerCheck()); // 添加范围挖掘检查 / Add nuker check
        checks.add(new PingSpoofCheck()); // 添加延迟欺骗检查 / Add ping spoof check
        checks.add(new PatternAnalysisCheck(patternDetector));
        // 可以添加更多检查...
        // Can add more checks...
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