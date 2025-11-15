package cn.aq3.antilag;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class AntiLagPlugin extends JavaPlugin {
    
    private static AntiLagPlugin instance;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Load configuration
        configManager = new ConfigManager(this);
        
        // Register commands
        getCommand("antilag").setExecutor(new AntiLagCommand(this));
        getCommand("opt").setExecutor(new OptimizeCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new OptimizationListener(this), this);
        
        // Start optimization tasks
        OptimizationManager.startOptimizationTasks(this);
        
        getLogger().info("AntiLag plugin enabled! Optimizing server performance.");
    }
    
    @Override
    public void onDisable() {
        OptimizationManager.stopOptimizationTasks();
        getLogger().info("AntiLag plugin disabled!");
    }
    
    public static AntiLagPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}