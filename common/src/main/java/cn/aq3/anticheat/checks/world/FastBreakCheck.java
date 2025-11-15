package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;

/**
 * 快速破坏检查 - 检测玩家过快破坏方块
 * Fast Break Check - Detect players breaking blocks too quickly
 */
public class FastBreakCheck extends Check {
    // 存储每个玩家正在破坏的方块信息
    // Store breaking block information for each player
    private final Map<String, BlockBreakInfo> breakingBlocks = new HashMap<>();
    
    // 默认方块破坏时间（毫秒）
    // Default block break time (milliseconds)
    private static final long DEFAULT_BREAK_TIME = 500;
    
    public FastBreakCheck() {
        super("FastBreak", "检测快速破坏方块作弊", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 检查玩家是否刚刚加入游戏，如果是则跳过检查
        // Check if player just joined the game, skip check if so
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 5000) { // 5秒内加入游戏的玩家不检查 / Don't check players who joined in the last 5 seconds
            return false;
        }
        
        // 获取玩家UUID
        // Get player UUID
        String playerUUID = playerData.getUuid().toString();
        
        // 检查是否有正在进行的破坏操作
        // Check if there's an ongoing breaking operation
        BlockBreakInfo breakInfo = breakingBlocks.get(playerUUID);
        if (breakInfo == null) {
            return false;
        }
        
        // 计算实际破坏时间
        // Calculate actual break time
        long actualBreakTime = System.currentTimeMillis() - breakInfo.getStartTime();
        
        // 获取方块类型对应的破坏时间
        // Get break time corresponding to block type
        long expectedBreakTime = getBlockBreakTime(breakInfo.getBlockType());
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 - (sensitivity - 1) * 0.1; // 灵敏度越高，阈值越严格 / Higher sensitivity means stricter threshold
        long minAllowedBreakTime = (long) (expectedBreakTime * thresholdMultiplier);
        
        // 检查是否破坏过快
        // Check if breaking too fast
        boolean isTooFast = actualBreakTime < minAllowedBreakTime;
        
        // 如果破坏完成，移除记录
        // Remove record if breaking is complete
        if (breakInfo.isCompleted()) {
            breakingBlocks.remove(playerUUID);
        }
        
        return isTooFast;
    }
    
    /**
     * 开始破坏方块
     * Start breaking block
     */
    public void startBreakingBlock(String playerUUID, String blockType) {
        breakingBlocks.put(playerUUID, new BlockBreakInfo(blockType, System.currentTimeMillis()));
    }
    
    /**
     * 完成破坏方块
     * Complete breaking block
     */
    public void completeBreakingBlock(String playerUUID) {
        BlockBreakInfo breakInfo = breakingBlocks.get(playerUUID);
        if (breakInfo != null) {
            breakInfo.setCompleted(true);
        }
    }
    
    /**
     * 获取方块破坏时间
     * Get block break time
     */
    private long getBlockBreakTime(String blockType) {
        // 简化实现 - 实际应用中需要根据不同方块类型返回不同的破坏时间
        // Simplified implementation - in practice need to return different break times for different block types
        switch (blockType.toLowerCase()) {
            case "stone":
                return 5000; // 石头需要5秒 / Stone takes 5 seconds
            case "dirt":
                return 1000; // 泥土需要1秒 / Dirt takes 1 second
            case "wood":
                return 2000; // 木头需要2秒 / Wood takes 2 seconds
            default:
                return DEFAULT_BREAK_TIME; // 默认破坏时间 / Default break time
        }
    }
    
    /**
     * 方块破坏信息类
     * Block break information class
     */
    private static class BlockBreakInfo {
        private final String blockType;
        private final long startTime;
        private boolean completed;
        
        public BlockBreakInfo(String blockType, long startTime) {
            this.blockType = blockType;
            this.startTime = startTime;
            this.completed = false;
        }
        
        public String getBlockType() {
            return blockType;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}