package cn.aq3.anticheat.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配置管理器
 * Configuration manager
 */
public class ConfigManager {
    private final Map<String, Object> configValues = new HashMap<>();
    private final Set<String> immunePlayers = new HashSet<>();
    private final Set<String> admins = new HashSet<>();
    
    public ConfigManager() {
        loadDefaultConfig();
    }
    
    /**
     * 加载默认配置
     * Load default configuration
     */
    private void loadDefaultConfig() {
        // 移动检查
        // Movement checks
        configValues.put("checks.movement.enabled", true);
        configValues.put("checks.movement.speed.enabled", true);
        configValues.put("checks.movement.speed.max_violations", 10);
        configValues.put("checks.movement.fly.enabled", true);
        configValues.put("checks.movement.fly.max_violations", 10);
        configValues.put("checks.movement.jesus.enabled", true);
        configValues.put("checks.movement.jesus.max_violations", 10);
        configValues.put("checks.movement.timer.enabled", true);
        configValues.put("checks.movement.timer.max_violations", 10);
        configValues.put("checks.movement.phase.enabled", true); // 添加相位检查配置 / Add phase check configuration
        configValues.put("checks.movement.phase.max_violations", 10);
        configValues.put("checks.movement.velocity.enabled", true); // 添加速度检查配置 / Add velocity check configuration
        configValues.put("checks.movement.velocity.max_violations", 10);
        
        // 战斗检查
        // Combat checks
        configValues.put("checks.combat.enabled", true);
        configValues.put("checks.combat.killaura.enabled", true);
        configValues.put("checks.combat.killaura.max_violations", 10);
        configValues.put("checks.combat.reach.enabled", true);
        configValues.put("checks.combat.reach.max_violations", 10);
        configValues.put("checks.combat.autoclicker.enabled", true);
        configValues.put("checks.combat.autoclicker.max_violations", 10);
        configValues.put("checks.combat.criticals.enabled", true); // 添加暴击检查配置 / Add criticals check configuration
        configValues.put("checks.combat.criticals.max_violations", 10);
        configValues.put("checks.combat.entityinteract.enabled", true); // 添加实体交互检查配置 / Add entity interact check configuration
        configValues.put("checks.combat.entityinteract.max_violations", 10);
        
        // 物品检查
        // Inventory checks
        configValues.put("checks.inventory.enabled", true);
        configValues.put("checks.inventory.fastuse.enabled", true);
        configValues.put("checks.inventory.fastuse.max_violations", 10);
        configValues.put("checks.inventory.inventorymove.enabled", true); // 添加背包移动检查配置 / Add inventory move check configuration
        configValues.put("checks.inventory.inventorymove.max_violations", 10);
        
        // 世界检查
        // World checks
        configValues.put("checks.world.enabled", true);
        configValues.put("checks.world.scaffold.enabled", true); // 添加脚手架检查配置 / Add scaffold check configuration
        configValues.put("checks.world.scaffold.max_violations", 10);
        configValues.put("checks.world.fastbreak.enabled", true); // 添加快速破坏检查配置 / Add fast break check configuration
        configValues.put("checks.world.fastbreak.max_violations", 10);
        configValues.put("checks.world.nuker.enabled", true); // 添加范围挖掘检查配置 / Add nuker check configuration
        configValues.put("checks.world.nuker.max_violations", 10);
        
        // 玩家检查
        // Player checks
        configValues.put("checks.player.enabled", true);
        configValues.put("checks.player.nofall.enabled", true);
        configValues.put("checks.player.nofall.max_violations", 10);
        configValues.put("checks.player.regen.enabled", true); // 添加生命值恢复检查配置 / Add regen check configuration
        configValues.put("checks.player.regen.max_violations", 10);
        configValues.put("checks.player.chat.enabled", true); // 添加聊天检查配置 / Add chat check configuration
        configValues.put("checks.player.chat.max_violations", 10);
        
        // 网络检查
        // Network checks
        configValues.put("checks.network.enabled", true);
        configValues.put("checks.network.pingspoof.enabled", true); // 添加延迟欺骗检查配置 / Add ping spoof check configuration
        configValues.put("checks.network.pingspoof.max_violations", 10);
        
        // 处罚设置
        // Punishment settings
        configValues.put("punishments.enabled", true);
        configValues.put("punishments.kick_threshold", 100);
        configValues.put("punishments.ban_threshold", 500);
        
        // 管理员设置
        // Admin settings
        configValues.put("punishments.ban_on_detection", true);
        configValues.put("punishments.ban_duration", -1);
        configValues.put("punishments.ban_reason", "[AQAC] 作弊行为检测");
        configValues.put("punishments.kick_message", "[AQAC] 作弊行为检测");
        configValues.put("punishments.use_advancedban", true); // 添加AdvancedBan集成配置 / Add AdvancedBan integration configuration
        configValues.put("punishments.advancedban_temp_ban_duration", 86400000L); // 24小时 / 24 hours
        configValues.put("punishments.advancedban_ban_reason", "[AQAC] 作弊行为检测"); // AdvancedBan封禁原因 / AdvancedBan ban reason
        configValues.put("punishments.advancedban_kick_reason", "[AQAC] 作弊行为检测"); // AdvancedBan踢出原因 / AdvancedBan kick reason
        
        // 硬件标识设置
        // Hardware identifier settings
        configValues.put("hwid.ban_hwid_on_ban", true);
        
        // 灵敏度设置 (1-5, 5为最高灵敏度)
        // Sensitivity settings (1-5, 5 is highest sensitivity)
        configValues.put("sensitivity", 3);
        
        // 默认管理员和免疫玩家
        // Default admins and immune players
        admins.add("admin_player_name");
        admins.add("another_admin_name");
        immunePlayers.add("trusted_player_name");
        immunePlayers.add("another_trusted_player");
    }
    
    /**
     * 从输入流加载配置
     * Load configuration from input stream
     */
    public void loadConfig(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> loadedConfig = yaml.load(inputStream);
        
        if (loadedConfig != null) {
            flattenMap("", loadedConfig, configValues);
        }
    }
    
    /**
     * 从文件加载配置
     * Load configuration from file
     */
    public void loadConfigFromFile(File configFile) {
        try {
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    loadConfig(fis);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load config file: " + e.getMessage());
        }
    }
    
    /**
     * 加载管理员配置
     * Load admin configuration
     */
    public void loadAdminConfig(File adminConfigFile) {
        try {
            if (adminConfigFile.exists()) {
                Yaml yaml = new Yaml();
                try (FileInputStream fis = new FileInputStream(adminConfigFile)) {
                    Map<String, Object> adminConfig = yaml.load(fis);
                    
                    if (adminConfig != null) {
                        // 加载管理员列表
                        Object adminsObj = adminConfig.get("admins");
                        if (adminsObj instanceof Iterable) {
                            admins.clear();
                            for (Object admin : (Iterable<?>) adminsObj) {
                                admins.add(admin.toString());
                            }
                        }
                        
                        // 加载免疫玩家列表
                        Object immunePlayersObj = adminConfig.get("immune-players");
                        if (immunePlayersObj instanceof Iterable) {
                            immunePlayers.clear();
                            for (Object immunePlayer : (Iterable<?>) immunePlayersObj) {
                                immunePlayers.add(immunePlayer.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load admin config file: " + e.getMessage());
        }
    }
    
    /**
     * 将嵌套的Map转换为扁平的键值对
     * Convert nested map to flat key-value pairs
     */
    private void flattenMap(String prefix, Map<String, Object> nestedMap, Map<String, Object> flatMap) {
        for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                // 递归处理嵌套Map
                // Recursively process nested maps
                flattenMap(key, (Map<String, Object>) value, flatMap);
            } else {
                // 直接放入扁平Map
                // Put directly into flat map
                flatMap.put(key, value);
            }
        }
    }
    
    /**
     * 获取配置值
     * Get configuration value
     */
    public Object get(String key) {
        return configValues.getOrDefault(key, null);
    }
    
    /**
     * 获取字符串配置值
     * Get string configuration value
     */
    public String getString(String key, String defaultValue) {
        Object value = configValues.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * 获取整数配置值
     * Get integer configuration value
     */
    public int getInt(String key, int defaultValue) {
        Object value = configValues.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    /**
     * 获取长整数配置值
     * Get long configuration value
     */
    public long getLong(String key, long defaultValue) {
        Object value = configValues.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    /**
     * 获取布尔配置值
     * Get boolean configuration value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = configValues.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
    
    /**
     * 检查玩家是否免疫
     * Check if player is immune
     */
    public boolean isImmune(String playerName) {
        return immunePlayers.contains(playerName);
    }
    
    /**
     * 检查玩家是否为管理员
     * Check if player is admin
     */
    public boolean isAdmin(String playerName) {
        return admins.contains(playerName);
    }
}