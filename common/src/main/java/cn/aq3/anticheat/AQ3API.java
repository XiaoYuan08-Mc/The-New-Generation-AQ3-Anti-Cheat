package cn.aq3.anticheat;

import cn.aq3.anticheat.checks.CheckManager;
import cn.aq3.anticheat.config.ConfigManager;
import cn.aq3.anticheat.hwid.HWIDManager;
import cn.aq3.anticheat.logging.CheatLogger;
import cn.aq3.anticheat.player.PlayerDataManager;
import cn.aq3.anticheat.punishment.PunishmentManager;
import cn.aq3.anticheat.violations.ViolationManager;
import cn.aq3.anticheat.world.WorldManager;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * 新一代反作弊AQ3主API类
 * Next Generation Anti-Cheat AQ3 Main API Class
 * 
 * 新一代反作弊系统，具有以下特性：
 * - 精确的物理引擎模拟
 * - 世界复制系统
 * - 多种作弊检测
 * - 机器学习模式识别
 * - 可配置的处罚系统
 * - 完整的日志记录
 */
public class AQ3API {
    private static AQ3API instance;
    
    private PlayerDataManager playerDataManager;
    private WorldManager worldManager;
    private CheckManager checkManager;
    private ViolationManager violationManager;
    private ConfigManager configManager;
    private PunishmentManager punishmentManager;
    private CheatLogger cheatLogger;
    private HWIDManager hwidManager; // 硬件标识管理器 / Hardware Identification Manager
    private Object bukkitAPI; // Bukkit API引用 / Bukkit API reference

    /**
     * 获取AQ3反作弊实例
     * Get AQ3 anti-cheat instance
     *
     * @return AQ3API实例 / AQ3API instance
     */
    public static AQ3API getInstance() {
        return instance;
    }

    @ApiStatus.Internal
    public static void setInstance(AQ3API api) {
        instance = api;
    }

    /**
     * 设置Bukkit API引用
     * Set Bukkit API reference
     */
    public void setBukkitAPI(Object bukkitAPI) {
        this.bukkitAPI = bukkitAPI;
    }

    /**
     * 获取Bukkit API引用
     * Get Bukkit API reference
     */
    public Object getBukkitAPI() {
        return bukkitAPI;
    }
    
    /**
     * 复制默认配置文件
     * Copy default configuration file
     */
    private void copyDefaultConfig(String resourcePath, File configFile) {
        // 如果配置文件已存在，则不覆盖
        // If config file already exists, don't overwrite
        if (configFile.exists()) {
            return;
        }
        
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                Files.copy(inputStream, configFile.toPath());
            }
        } catch (IOException e) {
            System.err.println("Failed to copy default config file: " + e.getMessage());
        }
    }

    /**
     * 初始化反作弊系统
     * Initialize anti-cheat system
     */
    public void initialize() {
        // 初始化系统组件
        // Initialize system components
        playerDataManager = new PlayerDataManager();
        worldManager = new WorldManager();
        checkManager = new CheckManager();
        violationManager = new ViolationManager();
        configManager = new ConfigManager();
        punishmentManager = new PunishmentManager(100, 500);
        cheatLogger = new CheatLogger("logs/cheat.log");
        hwidManager = new HWIDManager("plugins/AQ3AntiCheat/hwid.dat"); // 初始化硬件标识管理器 / Initialize Hardware Identification Manager
        
        // 加载配置文件
        // Load configuration files
        // 在实际实现中，这里需要加载实际的配置文件
        // In actual implementation, this needs to load actual configuration files
        File pluginDir = new File("plugins/AQ3AntiCheat");
        pluginDir.mkdirs();
        
        File configFile = new File(pluginDir, "config.yml");
        File adminConfigFile = new File(pluginDir, "admin-config.yml");
        
        // 如果配置文件不存在，则从资源文件中复制默认配置
        // If config files don't exist, copy default configs from resources
        copyDefaultConfig("/config.yml", configFile);
        copyDefaultConfig("/admin-config.yml", adminConfigFile);
        
        configManager.loadConfigFromFile(configFile);
        configManager.loadAdminConfig(adminConfigFile);
        
        cheatLogger.logEvent("AQ3 Anti-Cheat system initialized");
    }

    /**
     * 关闭反作弊系统
     * Shutdown anti-cheat system
     */
    public void shutdown() {
        // 清理资源
        // Cleanup resources
        playerDataManager = null;
        worldManager = null;
        checkManager = null;
        violationManager = null;
        configManager = null;
        punishmentManager = null;
        bukkitAPI = null;
        
        if (cheatLogger != null) {
            cheatLogger.logEvent("AQ3 Anti-Cheat system shutdown");
            cheatLogger.close();
            cheatLogger = null;
        }
        
        if (hwidManager != null) {
            hwidManager.saveData(); // 保存硬件标识数据 / Save hardware identifier data
            hwidManager = null;
        }
    }
    
    // Getters for system components
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public WorldManager getWorldManager() {
        return worldManager;
    }
    
    public CheckManager getCheckManager() {
        return checkManager;
    }
    
    public ViolationManager getViolationManager() {
        return violationManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }
    
    public CheatLogger getCheatLogger() {
        return cheatLogger;
    }
    
    public HWIDManager getHWIDManager() {
        return hwidManager;
    }
}