package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * X射线检查 - 检测玩家可能使用X射线作弊
 * Xray Check - Detect players possibly using Xray cheats
 */
public class XrayCheck extends Check {
    // 存储每个玩家的挖掘数据
    // Store mining data for each player
    private final Map<String, MiningData> miningDataMap = new HashMap<>();
    
    // 稀有方块类型
    // Rare block types
    private static final Set<String> RARE_BLOCKS = new HashSet<>();
    
    static {
        RARE_BLOCKS.add("diamond_ore");
        RARE_BLOCKS.add("deepslate_diamond_ore");
        RARE_BLOCKS.add("emerald_ore");
        RARE_BLOCKS.add("deepslate_emerald_ore");
        RARE_BLOCKS.add("ancient_debris");
        RARE_BLOCKS.add("gold_ore");
        RARE_BLOCKS.add("deepslate_gold_ore");
    }
    
    public XrayCheck() {
        super("Xray", "检测X射线作弊", true, 15);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        // 获取灵敏度设置
        // Get sensitivity setting
        int sensitivity = AQ3API.getInstance().getConfigManager().getInt("sensitivity", 3);
        
        // 检查玩家是否刚刚加入游戏
        // Check if player just joined the game
        long timeSinceJoin = System.currentTimeMillis() - playerData.getJoinTime();
        if (timeSinceJoin < 10000) { // 10秒内加入游戏的玩家不检查
            return false;
        }
        
        String playerUUID = playerData.getUuid().toString();
        MiningData data = miningDataMap.get(playerUUID);
        if (data == null) {
            return false;
        }
        
        // 检查是否有足够的数据进行分析
        // Check if there's enough data for analysis
        if (data.getTotalBlocksMined() < 50) {
            return false;
        }
        
        // 计算稀有方块比例
        // Calculate rare block ratio
        double rareBlockRatio = (double) data.getRareBlocksMined() / data.getTotalBlocksMined();
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double threshold = 0.05 - (sensitivity - 1) * 0.008; // 灵敏度越高，阈值越低
        
        // 检查稀有方块比例是否异常高
        // Check if rare block ratio is abnormally high
        if (rareBlockRatio > threshold) {
            // 进一步检查挖掘模式
            // Further check mining pattern
            return checkMiningPattern(data);
        }
        
        return false;
    }
    
    /**
     * 检查挖掘模式
     * Check mining pattern
     */
    private boolean checkMiningPattern(MiningData data) {
        // 检查是否连续挖掘稀有方块
        // Check for consecutive rare block mining
        if (data.getConsecutiveRareBlocks() >= 3) {
            return true;
        }
        
        // 检查是否在短时间内挖掘多个稀有方块
        // Check for multiple rare blocks mined in a short time
        long timeSinceFirstRare = System.currentTimeMillis() - data.getFirstRareBlockTime();
        if (timeSinceFirstRare < 30000 && data.getRareBlocksInWindow() >= 5) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 记录挖掘方块
     * Record mined block
     */
    public void recordBlockMined(String playerUUID, String blockType) {
        MiningData data = miningDataMap.computeIfAbsent(playerUUID, k -> new MiningData());
        
        data.incrementTotalBlocks();
        
        if (RARE_BLOCKS.contains(blockType.toLowerCase())) {
            data.incrementRareBlocks();
            data.incrementConsecutiveRareBlocks();
            data.addRareBlockInWindow();
            
            if (data.getFirstRareBlockTime() == 0) {
                data.setFirstRareBlockTime(System.currentTimeMillis());
            }
        } else {
            data.resetConsecutiveRareBlocks();
        }
        
        // 清理旧数据
        // Clean up old data
        data.cleanupOldRareBlocks();
    }
    
    /**
     * 重置玩家数据
     * Reset player data
     */
    public void resetPlayerData(String playerUUID) {
        miningDataMap.remove(playerUUID);
    }
    
    /**
     * 挖掘数据类
     * Mining data class
     */
    private static class MiningData {
        private int totalBlocksMined;
        private int rareBlocksMined;
        private int consecutiveRareBlocks;
        private int rareBlocksInWindow;
        private long firstRareBlockTime;
        private final Map<Long, Boolean> recentRareBlocks = new HashMap<>();
        
        public void incrementTotalBlocks() {
            totalBlocksMined++;
        }
        
        public void incrementRareBlocks() {
            rareBlocksMined++;
        }
        
        public void incrementConsecutiveRareBlocks() {
            consecutiveRareBlocks++;
        }
        
        public void resetConsecutiveRareBlocks() {
            consecutiveRareBlocks = 0;
        }
        
        public void addRareBlockInWindow() {
            rareBlocksInWindow++;
            recentRareBlocks.put(System.currentTimeMillis(), true);
        }
        
        public void cleanupOldRareBlocks() {
            long cutoffTime = System.currentTimeMillis() - 30000; // 30秒窗口
            recentRareBlocks.entrySet().removeIf(entry -> entry.getKey() < cutoffTime);
            rareBlocksInWindow = recentRareBlocks.size();
            
            if (recentRareBlocks.isEmpty()) {
                firstRareBlockTime = 0;
            }
        }
        
        public int getTotalBlocksMined() {
            return totalBlocksMined;
        }
        
        public int getRareBlocksMined() {
            return rareBlocksMined;
        }
        
        public int getConsecutiveRareBlocks() {
            return consecutiveRareBlocks;
        }
        
        public int getRareBlocksInWindow() {
            return rareBlocksInWindow;
        }
        
        public long getFirstRareBlockTime() {
            return firstRareBlockTime;
        }
        
        public void setFirstRareBlockTime(long firstRareBlockTime) {
            this.firstRareBlockTime = firstRareBlockTime;
        }
    }
}
