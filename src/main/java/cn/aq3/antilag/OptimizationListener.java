package cn.aq3.antilag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.entity.Item;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Bukkit;

public class OptimizationListener implements Listener {
    
    private final AntiLagPlugin plugin;
    
    public OptimizationListener(AntiLagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Handle player join events
        // Adjust view distance when players are online
        Player player = event.getPlayer();
        for (World world : Bukkit.getWorlds()) {
            world.setViewDistance(ConfigManager.MAX_VIEW_DISTANCE);
        }
        
        // Initialize player activity tracking
        OptimizationManager.updatePlayerActivity(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Handle player quit events
        // Potentially enable guardian mode if this was the last player
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                // Set view distance to minimum when no players are online
                for (World world : Bukkit.getWorlds()) {
                    world.setViewDistance(ConfigManager.MIN_VIEW_DISTANCE);
                }
            }
        }, 20L); // Delay by 1 second to ensure player has fully left
        
        // Remove player from AFK tracking
        OptimizationManager.updatePlayerActivity(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Update player activity on movement
        Player player = event.getPlayer();
        OptimizationManager.updatePlayerActivity(player.getUniqueId());
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        
        // Limit elytra usage if enabled
        if (ConfigManager.LIMIT_ELYTRA && entity instanceof Player) {
            // Implementation would track and limit elytra usage
        }
        
        // Limit trident usage if enabled
        if (ConfigManager.LIMIT_TRIDENT && entity instanceof Player) {
            // Implementation would track and limit trident usage
        }
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Handle chunk loading events for optimization
        // Could implement entity limiting per chunk
    }
}