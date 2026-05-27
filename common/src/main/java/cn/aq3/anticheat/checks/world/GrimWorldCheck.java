package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GrimAC风格的综合世界交互检测
 * 检测方块操作相关的作弊
 */
public class GrimWorldCheck extends Check {
    
    private final Map<UUID, WorldInteractionData> interactionData = new HashMap<>();
    
    // 方块操作相关常量
    private static final double MAX_BLOCK_REACH = 5.0;
    private static final int MAX_BLOCKS_PER_SECOND = 15;
    private static final long MIN_DIG_TIME = 100; // 最小挖掘时间（毫秒）
    
    public GrimWorldCheck() {
        super("GrimWorld", "GrimAC综合世界交互检测", true, 10);
    }
    
    @Override
    public boolean performCheck(PlayerData playerData) {
        if (!isEnabled()) {
            return false;
        }
        
        if (playerData == null) {
            return false;
        }
        
        UUID playerUUID = playerData.getUuid();
        WorldInteractionData data = interactionData.computeIfAbsent(playerUUID, k -> new WorldInteractionData());
        
        // 检查快速放置
        if (checkFastPlace(data, playerData)) {
            return true;
        }
        
        // 检查快速破坏
        if (checkFastBreak(data, playerData)) {
            return true;
        }
        
        // 检查 scaffold（自动搭路）
        if (checkScaffold(data, playerData)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 检查快速放置
     */
    private boolean checkFastPlace(WorldInteractionData data, PlayerData playerData) {
        data.cleanupOldPlaces(System.currentTimeMillis());
        
        int placesLastSecond = data.getPlacesLastSecond();
        if (placesLastSecond > MAX_BLOCKS_PER_SECOND) {
            data.incrementFastPlaceFlag();
            if (data.getFastPlaceFlags() >= 3) {
                return true;
            }
        } else {
            data.decrementFastPlaceFlags();
        }
        
        return false;
    }
    
    /**
     * 检查快速破坏
     */
    private boolean checkFastBreak(WorldInteractionData data, PlayerData playerData) {
        // 获取最后挖掘时间
        long lastDigTime = playerData.getLastDigTime();
        long timeSinceDig = System.currentTimeMillis() - lastDigTime;
        
        // 如果挖掘时间过短，可能有问题
        if (timeSinceDig < MIN_DIG_TIME && lastDigTime > 0) {
            data.incrementFastBreakFlag();
            if (data.getFastBreakFlags() >= 3) {
                return true;
            }
        } else {
            data.decrementFastBreakFlags();
        }
        
        return false;
    }
    
    /**
     * 检查脚手架作弊
     */
    private boolean checkScaffold(WorldInteractionData data, PlayerData playerData) {
        // 获取最近的方块放置频率
        int recentPlaces = data.getRecentPlaceCount();
        
        // 检查是否在合理范围内
        if (recentPlaces > 20) { // 超过每秒20个方块
            // 检查玩家状态
            if (!playerData.isOnGround() && !playerData.isFlyingWithElytra()) {
                // 不在地面上放置方块，可能是Scaffold
                data.incrementScaffoldFlag();
                if (data.getScaffoldFlags() >= 2) {
                    return true;
                }
            }
        }
        
        data.decrementScaffoldFlags();
        return false;
    }
    
    /**
     * 记录方块放置
     */
    public void recordBlockPlace(UUID playerUUID, double reach) {
        WorldInteractionData data = interactionData.computeIfAbsent(playerUUID, k -> new WorldInteractionData());
        data.recordPlace(System.currentTimeMillis(), reach);
    }
    
    /**
     * 获取玩家的交互数据
     */
    public WorldInteractionData getInteractionData(UUID playerUUID) {
        return interactionData.get(playerUUID);
    }
    
    /**
     * 世界交互数据结构
     */
    public static class WorldInteractionData {
        private final Map<Long, Double> placeTimestamps = new HashMap<>();
        private int fastPlaceFlags = 0;
        private int fastBreakFlags = 0;
        private int scaffoldFlags = 0;
        
        public void recordPlace(long timestamp, double reach) {
            placeTimestamps.put(timestamp, reach);
        }
        
        public void cleanupOldPlaces(long currentTime) {
            long oneSecondAgo = currentTime - 1000;
            placeTimestamps.entrySet().removeIf(entry -> entry.getKey() < oneSecondAgo);
        }
        
        public int getPlacesLastSecond() {
            return placeTimestamps.size();
        }
        
        public int getRecentPlaceCount() {
            return placeTimestamps.size();
        }
        
        public void incrementFastPlaceFlag() {
            fastPlaceFlags++;
        }
        
        public void decrementFastPlaceFlags() {
            fastPlaceFlags = Math.max(0, fastPlaceFlags - 1);
        }
        
        public int getFastPlaceFlags() {
            return fastPlaceFlags;
        }
        
        public void incrementFastBreakFlag() {
            fastBreakFlags++;
        }
        
        public void decrementFastBreakFlags() {
            fastBreakFlags = Math.max(0, fastBreakFlags - 1);
        }
        
        public int getFastBreakFlags() {
            return fastBreakFlags;
        }
        
        public void incrementScaffoldFlag() {
            scaffoldFlags++;
        }
        
        public void decrementScaffoldFlags() {
            scaffoldFlags = Math.max(0, scaffoldFlags - 1);
        }
        
        public int getScaffoldFlags() {
            return scaffoldFlags;
        }
        
        public void reset() {
            placeTimestamps.clear();
            fastPlaceFlags = 0;
            fastBreakFlags = 0;
            scaffoldFlags = 0;
        }
    }
}
