package cn.aq3.anticheat.network;

import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GrimAC风格的延迟补偿系统
 * 处理网络延迟和客户端-服务器时间同步问题
 */
public class GrimLatencyCompensation {
    
    // 玩家延迟数据
    private final Map<UUID, LatencyData> latencyData = new HashMap<>();
    
    // 最大允许的延迟差异（毫秒）
    private static final long MAX_LATENCY_DIFF = 2000;
    
    // 延迟平滑因子
    private static final double SMOOTHING_FACTOR = 0.9;
    
    /**
     * 获取或创建玩家的延迟数据
     */
    public LatencyData getLatencyData(UUID playerUUID) {
        return latencyData.computeIfAbsent(playerUUID, k -> new LatencyData());
    }
    
    /**
     * 更新玩家延迟
     */
    public void updateLatency(UUID playerUUID, long ping) {
        LatencyData data = getLatencyData(playerUUID);
        
        // 使用移动平均来平滑延迟
        long oldPing = data.getPing();
        long newPing = (long) (oldPing * SMOOTHING_FACTOR + ping * (1 - SMOOTHING_FACTOR));
        
        data.setPing(newPing);
        data.setLastUpdate(System.currentTimeMillis());
    }
    
    /**
     * 检查延迟是否异常
     */
    public boolean isLatencyAbnormal(UUID playerUUID) {
        LatencyData data = latencyData.get(playerUUID);
        if (data == null) {
            return false;
        }
        
        // 检查延迟是否过高
        if (data.getPing() > MAX_LATENCY_DIFF) {
            return true;
        }
        
        // 检查延迟是否变化过大
        long pingDiff = Math.abs(data.getPing() - data.getLastPing());
        if (pingDiff > 500) { // 延迟突变超过500ms
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取补偿后的延迟
     */
    public long getCompensatedLatency(UUID playerUUID) {
        LatencyData data = latencyData.get(playerUUID);
        if (data == null) {
            return 0;
        }
        return data.getPing();
    }
    
    /**
     * 移除玩家延迟数据
     */
    public void removeLatencyData(UUID playerUUID) {
        latencyData.remove(playerUUID);
    }
    
    /**
     * 延迟数据结构
     */
    public static class LatencyData {
        private long ping = 0;
        private long lastPing = 0;
        private long lastUpdate = 0;
        private int packetLossCount = 0;
        private int outOfOrderCount = 0;
        
        public long getPing() {
            return ping;
        }
        
        public void setPing(long ping) {
            this.lastPing = this.ping;
            this.ping = ping;
        }
        
        public long getLastPing() {
            return lastPing;
        }
        
        public long getLastUpdate() {
            return lastUpdate;
        }
        
        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
        
        public int getPacketLossCount() {
            return packetLossCount;
        }
        
        public void incrementPacketLoss() {
            this.packetLossCount++;
        }
        
        public int getOutOfOrderCount() {
            return outOfOrderCount;
        }
        
        public void incrementOutOfOrder() {
            this.outOfOrderCount++;
        }
        
        public void reset() {
            this.packetLossCount = 0;
            this.outOfOrderCount = 0;
        }
    }
}
