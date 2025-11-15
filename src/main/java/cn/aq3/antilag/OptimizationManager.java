package cn.aq3.antilag;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OptimizationManager {
    
    private static BukkitTask itemCleanupTask;
    private static BukkitTask entityCleanupTask;
    private static BukkitTask viewDistanceTask;
    private static BukkitTask guardianModeTask;
    private static BukkitTask afkCheckTask;
    private static BukkitTask memoryManagementTask;
    private static BukkitTask worldSaveTask;
    
    // AFK tracking
    private static Map<UUID, Long> playerActivity = new HashMap<>();
    
    public static void startOptimizationTasks(AntiLagPlugin plugin) {
        // Schedule item cleanup task
        itemCleanupTask = Bukkit.getScheduler().runTaskTimer(
            plugin, 
            () -> ItemCleanupManager.cleanupItems(), 
            ConfigManager.ITEM_CHECK_INTERVAL, 
            ConfigManager.ITEM_CHECK_INTERVAL
        );
        
        // Schedule entity cleanup task
        entityCleanupTask = Bukkit.getScheduler().runTaskTimer(
            plugin,
            () -> ItemCleanupManager.cleanupEntities(),
            200L, // 10 seconds
            200L  // 10 seconds
        );
        
        // Schedule AFK check task if enabled
        if (ConfigManager.AFK_KICK_ENABLED) {
            afkCheckTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> checkAFKPlayers(plugin),
                1200L, // 1 minute
                1200L  // 1 minute
            );
        }
        
        // Schedule guardian mode task if enabled
        if (ConfigManager.GUARDIAN_MODE_ENABLED) {
            guardianModeTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> checkGuardianMode(plugin),
                ConfigManager.GUARDIAN_MODE_DELAY * 20L,
                ConfigManager.GUARDIAN_MODE_DELAY * 20L
            );
        }
        
        // Schedule memory management task
        if (ConfigManager.AUTO_GC_ENABLED) {
            memoryManagementTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> manageMemory(),
                6000L, // 5 minutes
                6000L  // 5 minutes
            );
        }
        
        // Schedule world save task
        if (ConfigManager.AUTO_SAVE_WORLDS) {
            worldSaveTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> saveWorlds(),
                6000L, // 5 minutes
                6000L  // 5 minutes
            );
        }
    }
    
    public static void stopOptimizationTasks() {
        if (itemCleanupTask != null) itemCleanupTask.cancel();
        if (entityCleanupTask != null) entityCleanupTask.cancel();
        if (viewDistanceTask != null) viewDistanceTask.cancel();
        if (guardianModeTask != null) guardianModeTask.cancel();
        if (afkCheckTask != null) afkCheckTask.cancel();
        if (memoryManagementTask != null) memoryManagementTask.cancel();
        if (worldSaveTask != null) worldSaveTask.cancel();
        
        // Clear AFK tracking
        playerActivity.clear();
    }
    
    // Update player activity when they move
    public static void updatePlayerActivity(UUID playerUUID) {
        playerActivity.put(playerUUID, System.currentTimeMillis());
    }
    
    private static void checkAFKPlayers(AntiLagPlugin plugin) {
        // Implementation for checking and kicking AFK players
        long currentTime = System.currentTimeMillis();
        long kickTimeMs = ConfigManager.AFK_KICK_TIME * 1000L;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Skip players with permission
            if (player.hasPermission("antilag.afk.exempt")) {
                continue;
            }
            
            UUID playerUUID = player.getUniqueId();
            Long lastActivity = playerActivity.get(playerUUID);
            
            if (lastActivity == null) {
                // First time tracking this player
                playerActivity.put(playerUUID, currentTime);
            } else if ((currentTime - lastActivity) > kickTimeMs) {
                // Kick AFK player
                player.kickPlayer("您因长时间挂机已被踢出服务器");
                playerActivity.remove(playerUUID);
            }
        }
    }
    
    private static void checkGuardianMode(AntiLagPlugin plugin) {
        // Implementation for guardian mode when no players are online
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            // Reduce server load when no players are online
            // This could include reducing view distance, stopping non-essential tasks, etc.
            
            // Set view distance to minimum
            for (World world : Bukkit.getWorlds()) {
                world.setViewDistance(ConfigManager.MIN_VIEW_DISTANCE);
            }
        } else {
            // Restore normal view distance
            for (World world : Bukkit.getWorlds()) {
                world.setViewDistance(ConfigManager.MAX_VIEW_DISTANCE);
            }
        }
    }
    
    private static void manageMemory() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        double memoryUsagePercentage = (double) usedMemory / maxMemory;
        
        if (memoryUsagePercentage > ConfigManager.GC_MEMORY_THRESHOLD) {
            System.gc();
        }
    }
    
    private static void saveWorlds() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            // Only save worlds when no players are online to reduce lag
            for (World world : Bukkit.getWorlds()) {
                world.save();
            }
        }
    }
}