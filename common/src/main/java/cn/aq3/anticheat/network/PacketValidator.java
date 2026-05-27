package cn.aq3.anticheat.network;

import cn.aq3.anticheat.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GrimAC风格的数据包验证系统
 * 验证客户端发送的数据包的合法性
 */
public class PacketValidator {
    
    // 玩家数据包数据
    private final Map<UUID, PacketData> packetData = new HashMap<>();
    
    // 最大允许的数据包频率（包/秒）
    private static final int MAX_PACKETS_PER_SECOND = 40;
    
    // 移动数据包序列号窗口
    private static final int SEQUENCE_WINDOW = 20;
    
    public PacketValidator() {
    }
    
    /**
     * 获取或创建玩家的数据包数据
     */
    public PacketData getPacketData(UUID playerUUID) {
        return packetData.computeIfAbsent(playerUUID, k -> new PacketData());
    }
    
    /**
     * 验证移动数据包
     */
    public boolean validateMovePacket(UUID playerUUID, long timestamp, double x, double y, double z, boolean onGround) {
        PacketData data = getPacketData(playerUUID);
        
        // 检查数据包频率
        if (!checkPacketFrequency(data, timestamp)) {
            return false;
        }
        
        // 检查序列号
        if (!checkSequenceNumber(data, timestamp)) {
            return false;
        }
        
        // 检查位置跳变
        if (!checkPositionJump(data, x, y, z)) {
            return false;
        }
        
        // 更新数据
        data.setLastTimestamp(timestamp);
        data.setLastX(x);
        data.setLastY(y);
        data.setLastZ(z);
        data.setLastOnGround(onGround);
        data.incrementPacketCount();
        
        return true;
    }
    
    /**
     * 检查数据包频率
     */
    private boolean checkPacketFrequency(PacketData data, long timestamp) {
        // 清理过期数据
        data.cleanupOldTimestamps(timestamp);
        
        // 获取过去1秒内的数据包数量
        int packetsLastSecond = data.getPacketsLastSecond();
        
        // 如果超过最大频率，可能有问题
        return packetsLastSecond <= MAX_PACKETS_PER_SECOND;
    }
    
    /**
     * 检查序列号（防止数据包重放攻击）
     */
    private boolean checkSequenceNumber(PacketData data, long timestamp) {
        // 获取上一个序列号
        long lastSeq = data.getLastSequence();
        
        // 简化处理：检查时间戳是否递增
        if (lastSeq > 0 && timestamp <= data.getLastTimestamp()) {
            data.incrementOutOfOrder();
            return false;
        }
        
        // 设置新序列号
        data.setLastSequence(timestamp);
        return true;
    }
    
    /**
     * 检查位置跳变
     */
    private boolean checkPositionJump(PacketData data, double x, double y, double z) {
        // 计算与上一次位置的差异
        double deltaX = Math.abs(x - data.getLastX());
        double deltaY = Math.abs(y - data.getLastY());
        double deltaZ = Math.abs(z - data.getLastZ());
        
        // 最大允许的瞬移距离（考虑网络延迟和正常移动）
        double maxJumpDistance = 10.0;
        
        // 检查是否超出正常范围
        double totalJump = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        if (totalJump > maxJumpDistance) {
            return false; // 可能是瞬移
        }
        
        // 检查垂直跳变（更严格的限制）
        if (deltaY > 5.0) {
            return false; // 垂直跳跃过大
        }
        
        return true;
    }
    
    /**
     * 重置玩家的数据包数据
     */
    public void resetPlayerData(UUID playerUUID) {
        packetData.remove(playerUUID);
    }
    
    /**
     * 数据包数据结构
     */
    public static class PacketData {
        private long lastTimestamp = 0;
        private long lastSequence = 0;
        private double lastX = 0, lastY = 0, lastZ = 0;
        private boolean lastOnGround = false;
        
        private final Map<Long, Boolean> recentTimestamps = new HashMap<>();
        private int packetsLastSecond = 0;
        private int outOfOrderCount = 0;
        private int totalPackets = 0;
        
        public long getLastTimestamp() {
            return lastTimestamp;
        }
        
        public void setLastTimestamp(long lastTimestamp) {
            this.lastTimestamp = lastTimestamp;
        }
        
        public long getLastSequence() {
            return lastSequence;
        }
        
        public void setLastSequence(long lastSequence) {
            this.lastSequence = lastSequence;
        }
        
        public double getLastX() {
            return lastX;
        }
        
        public void setLastX(double x) {
            this.lastX = x;
        }
        
        public double getLastY() {
            return lastY;
        }
        
        public void setLastY(double y) {
            this.lastY = y;
        }
        
        public double getLastZ() {
            return lastZ;
        }
        
        public void setLastZ(double z) {
            this.lastZ = z;
        }
        
        public boolean isLastOnGround() {
            return lastOnGround;
        }
        
        public void setLastOnGround(boolean lastOnGround) {
            this.lastOnGround = lastOnGround;
        }
        
        public void incrementPacketCount() {
            this.totalPackets++;
        }
        
        public int getTotalPackets() {
            return totalPackets;
        }
        
        public void incrementOutOfOrder() {
            this.outOfOrderCount++;
        }
        
        public int getOutOfOrderCount() {
            return outOfOrderCount;
        }
        
        public void addTimestamp(long timestamp) {
            recentTimestamps.put(timestamp, true);
        }
        
        public int getPacketsLastSecond() {
            return recentTimestamps.size();
        }
        
        public void cleanupOldTimestamps(long currentTime) {
            long oneSecondAgo = currentTime - 1000;
            recentTimestamps.entrySet().removeIf(entry -> entry.getKey() < oneSecondAgo);
        }
    }
}
