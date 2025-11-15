package cn.aq3.anticheat.checks;

import cn.aq3.anticheat.player.PlayerData;

/**
 * 反作弊检查基类
 * Base class for anti-cheat checks
 */
public abstract class Check {
    protected final String name;
    protected final String description;
    protected final boolean enabled;
    protected final int maxViolations;
    
    public Check(String name, String description, boolean enabled, int maxViolations) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.maxViolations = maxViolations;
    }
    
    /**
     * 执行检查
     * Perform check
     */
    public abstract boolean performCheck(PlayerData playerData);
    
    /**
     * 获取检查名称
     * Get check name
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取检查描述
     * Get check description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否启用
     * Check if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 获取最大违规次数
     * Get maximum violations
     */
    public int getMaxViolations() {
        return maxViolations;
    }
}