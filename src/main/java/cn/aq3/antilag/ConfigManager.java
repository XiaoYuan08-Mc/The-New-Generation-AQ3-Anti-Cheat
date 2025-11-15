package cn.aq3.antilag;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final AntiLagPlugin plugin;
    
    // Item removal settings
    public static int ITEM_DESPAWN_TIME;
    public static int ITEM_CHECK_INTERVAL;
    
    // Entity optimization settings
    public static int ARROW_DESPAWN_TIME;
    public static int ENTITY_DESPAWN_TIME;
    public static int MAX_ENTITIES_PER_CHUNK;
    
    // View distance settings
    public static int MIN_VIEW_DISTANCE;
    public static int MAX_VIEW_DISTANCE;
    public static boolean AUTO_VIEW_DISTANCE;
    
    // AFK settings
    public static int AFK_KICK_TIME;
    public static boolean AFK_KICK_ENABLED;
    
    // World saving settings
    public static boolean AUTO_SAVE_WORLDS;
    
    // Guardian mode settings
    public static int GUARDIAN_MODE_DELAY;
    public static boolean GUARDIAN_MODE_ENABLED;
    
    // Elytra and Trident settings
    public static boolean LIMIT_ELYTRA;
    public static boolean LIMIT_TRIDENT;
    
    // Memory management settings
    public static double GC_MEMORY_THRESHOLD;
    public static boolean AUTO_GC_ENABLED;
    
    // Hopper settings
    public static int HOPPER_TRANSFER_LIMIT;
    
    public ConfigManager(AntiLagPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        
        // Item removal settings
        ITEM_DESPAWN_TIME = config.getInt("item.despawn-time", 300); // 15 seconds
        ITEM_CHECK_INTERVAL = config.getInt("item.check-interval", 200); // 10 seconds
        
        // Entity optimization settings
        ARROW_DESPAWN_TIME = config.getInt("entity.arrow-despawn-time", 100); // 5 seconds
        ENTITY_DESPAWN_TIME = config.getInt("entity.general-despawn-time", 6000); // 5 minutes
        MAX_ENTITIES_PER_CHUNK = config.getInt("entity.max-per-chunk", 50);
        
        // View distance settings
        MIN_VIEW_DISTANCE = config.getInt("view-distance.min", 4);
        MAX_VIEW_DISTANCE = config.getInt("view-distance.max", 10);
        AUTO_VIEW_DISTANCE = config.getBoolean("view-distance.auto-adjust", true);
        
        // AFK settings
        AFK_KICK_TIME = config.getInt("afk.kick-time", 900); // 15 minutes
        AFK_KICK_ENABLED = config.getBoolean("afk.enabled", true);
        
        // World saving settings
        AUTO_SAVE_WORLDS = config.getBoolean("world.auto-save", true);
        
        // Guardian mode settings
        GUARDIAN_MODE_DELAY = config.getInt("guardian-mode.delay", 300); // 5 minutes
        GUARDIAN_MODE_ENABLED = config.getBoolean("guardian-mode.enabled", true);
        
        // Elytra and Trident settings
        LIMIT_ELYTRA = config.getBoolean("movement.limit-elytra", true);
        LIMIT_TRIDENT = config.getBoolean("movement.limit-trident", true);
        
        // Memory management settings
        GC_MEMORY_THRESHOLD = config.getDouble("memory.gc-threshold", 0.8); // 80%
        AUTO_GC_ENABLED = config.getBoolean("memory.auto-gc", true);
        
        // Hopper settings
        HOPPER_TRANSFER_LIMIT = config.getInt("hopper.transfer-limit", 1);
    }
}