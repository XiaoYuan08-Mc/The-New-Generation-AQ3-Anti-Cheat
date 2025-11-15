package cn.aq3.antilag;

import org.bukkit.entity.Item;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import java.util.List;
import java.util.ArrayList;

public class ItemCleanupManager {
    
    /**
     * Cleans up ground items based on configuration settings
     * @return Number of items removed
     */
    public static int cleanupItems() {
        int removedCount = 0;
        
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    Item item = (Item) entity;
                    
                    // Check if item has existed longer than configured time
                    if (item.getTicksLived() > ConfigManager.ITEM_DESPAWN_TIME) {
                        item.remove();
                        removedCount++;
                    }
                }
            }
        }
        
        return removedCount;
    }
    
    /**
     * Cleans up other entities like arrows based on configuration
     * @return Number of entities removed
     */
    public static int cleanupEntities() {
        int removedCount = 0;
        
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                // Clean up arrows
                if (entity instanceof Arrow) {
                    if (entity.getTicksLived() > ConfigManager.ARROW_DESPAWN_TIME) {
                        entity.remove();
                        removedCount++;
                    }
                }
                // Clean up other items if they've lived too long
                else if (!(entity instanceof org.bukkit.entity.Player) && 
                         !(entity instanceof org.bukkit.entity.Monster) &&
                         !(entity instanceof org.bukkit.entity.Animals)) {
                    if (entity.getTicksLived() > ConfigManager.ENTITY_DESPAWN_TIME) {
                        entity.remove();
                        removedCount++;
                    }
                }
            }
        }
        
        // Limit entities per chunk
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                Entity[] entities = chunk.getEntities();
                if (entities.length > ConfigManager.MAX_ENTITIES_PER_CHUNK) {
                    // Remove excess non-living entities
                    List<Entity> removable = new ArrayList<>();
                    for (Entity entity : entities) {
                        if (!(entity instanceof org.bukkit.entity.Player) && 
                            !(entity instanceof org.bukkit.entity.Monster) &&
                            !(entity instanceof org.bukkit.entity.Animals)) {
                            removable.add(entity);
                        }
                    }
                    
                    // Remove excess entities
                    while (removable.size() > 0 && 
                           (entities.length - removedCount) > ConfigManager.MAX_ENTITIES_PER_CHUNK) {
                        Entity toRemove = removable.remove(0);
                        toRemove.remove();
                        removedCount++;
                    }
                }
            }
        }
        
        return removedCount;
    }
}