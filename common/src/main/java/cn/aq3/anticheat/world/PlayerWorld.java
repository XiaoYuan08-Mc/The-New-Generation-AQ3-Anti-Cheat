package cn.aq3.anticheat.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家世界副本
 * Player world replica
 */
public class PlayerWorld {
    private final Map<Long, WorldBlock> blocks = new ConcurrentHashMap<>();
    
    /**
     * 添加方块到世界副本
     * Add block to world replica
     */
    public void addBlock(WorldBlock block) {
        long key = blockKey(block.getX(), block.getY(), block.getZ());
        blocks.put(key, block);
    }
    
    /**
     * 从世界副本中移除方块
     * Remove block from world replica
     */
    public void removeBlock(int x, int y, int z) {
        long key = blockKey(x, y, z);
        blocks.remove(key);
    }
    
    /**
     * 获取世界副本中的方块
     * Get block from world replica
     */
    public WorldBlock getBlock(int x, int y, int z) {
        long key = blockKey(x, y, z);
        return blocks.get(key);
    }
    
    /**
     * 检查世界副本中是否存在方块
     * Check if block exists in world replica
     */
    public boolean hasBlock(int x, int y, int z) {
        long key = blockKey(x, y, z);
        return blocks.containsKey(key);
    }
    
    /**
     * 生成方块键值
     * Generate block key
     */
    private long blockKey(int x, int y, int z) {
        return ((long)x & 0xFFFF) << 32 | ((long)z & 0xFFFF) << 16 | (long)y & 0xFFFF;
    }
}