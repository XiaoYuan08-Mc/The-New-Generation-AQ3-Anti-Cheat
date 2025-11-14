package cn.aq3.anticheat.checks.player;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 聊天检查 - 检测异常的聊天行为
 * Chat Check - Detect abnormal chat behavior
 */
public class ChatCheck extends Check {
    // 存储聊天时间戳的队列
    // Queue to store chat timestamps
    private final Queue<Long> chatTimestamps = new LinkedList<>();
    
    // 分析窗口大小（毫秒）
    // Analysis window size (milliseconds)
    private static final long WINDOW_SIZE = 1000; // 1秒 / 1 second
    
    // 最大允许的聊天频率（每秒）
    // Maximum allowed chat rate (per second)
    private static final double MAX_CHAT_RATE = 5.0;
    
    // 检测异常模式所需的最小聊天次数
    // Minimum number of chats required to detect abnormal patterns
    private static final int MIN_CHATS_FOR_ANALYSIS = 3;
    
    public ChatCheck() {
        super("Chat", "检测异常聊天行为", true, 10);
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
        if (timeSinceJoin < 10000) { // 10秒内加入游戏的玩家不检查 / Don't check players who joined in the last 10 seconds
            return false;
        }
        
        // 记录一次聊天
        // Record a chat
        long currentTime = System.currentTimeMillis();
        chatTimestamps.offer(currentTime);
        
        // 移除窗口外的旧聊天记录
        // Remove old chat records outside the window
        while (!chatTimestamps.isEmpty() && (currentTime - chatTimestamps.peek()) > WINDOW_SIZE) {
            chatTimestamps.poll();
        }
        
        // 如果聊天次数不足，不进行分析
        // If not enough chats, don't analyze
        if (chatTimestamps.size() < MIN_CHATS_FOR_ANALYSIS) {
            return false;
        }
        
        // 计算聊天频率（每秒聊天次数）
        // Calculate chat rate (chats per second)
        double chatRate = calculateChatRate();
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        double maxAllowedRate = MAX_CHAT_RATE * thresholdMultiplier;
        
        // 检查是否超出正常范围
        // Check if out of normal range
        boolean isTooFast = chatRate > maxAllowedRate;
        
        // 检查聊天间隔的一致性（机器人通常有非常一致的间隔）
        // Check consistency of chat intervals (bots usually have very consistent intervals)
        boolean isTooConsistent = false;
        if (chatTimestamps.size() >= 5) { // 需要至少5次聊天来分析一致性 / Need at least 5 chats to analyze consistency
            isTooConsistent = checkIntervalConsistency();
        }
        
        // 如果聊天频率过高或聊天间隔过于一致，则可能是机器人
        // If chat rate is too high or chat intervals are too consistent, may be bot
        return isTooFast || isTooConsistent;
    }
    
    /**
     * 计算聊天频率
     * Calculate chat rate
     */
    private double calculateChatRate() {
        if (chatTimestamps.size() < 2) {
            return 0;
        }
        
        long firstTimestamp = chatTimestamps.peek();
        long lastTimestamp = ((LinkedList<Long>) chatTimestamps).getLast();
        long timeWindow = lastTimestamp - firstTimestamp;
        
        if (timeWindow <= 0) {
            return 0;
        }
        
        // 计算每秒聊天次数
        // Calculate chats per second
        return (chatTimestamps.size() * 1000.0) / timeWindow;
    }
    
    /**
     * 检查聊天间隔一致性
     * Check chat interval consistency
     */
    private boolean checkIntervalConsistency() {
        if (chatTimestamps.size() < 3) {
            return false;
        }
        
        // 计算所有聊天间隔
        // Calculate all chat intervals
        long[] intervals = new long[chatTimestamps.size() - 1];
        Long[] timestamps = chatTimestamps.toArray(new Long[0]);
        
        for (int i = 0; i < intervals.length; i++) {
            intervals[i] = timestamps[i + 1] - timestamps[i];
        }
        
        // 计算平均间隔和标准差
        // Calculate average interval and standard deviation
        double average = 0;
        for (long interval : intervals) {
            average += interval;
        }
        average /= intervals.length;
        
        double variance = 0;
        for (long interval : intervals) {
            variance += Math.pow(interval - average, 2);
        }
        variance /= intervals.length;
        double standardDeviation = Math.sqrt(variance);
        
        // 如果标准差非常小，说明间隔非常一致，可能是机器人
        // If standard deviation is very small, intervals are very consistent, may be bot
        // 正常玩家的聊天间隔通常会有较大的变化
        // Normal player's chat intervals usually have larger variations
        return standardDeviation < 100; // 小于100毫秒的标准差被认为是过于一致的 / Standard deviation less than 100ms is considered too consistent
    }
}