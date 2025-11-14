package cn.aq3.anticheat.checks.world;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 脚手架检查 - 检测自动搭路作弊
 * Scaffold Check - Detect automatic bridging cheats
 */
public class ScaffoldCheck extends Check {
    // 存储放置方块的时间戳
    // Queue to store block placement timestamps
    private final Queue<Long> placeTimestamps = new LinkedList<>();
    
    // 分析窗口大小（毫秒）
    // Analysis window size (milliseconds)
    private static final long WINDOW_SIZE = 1000; // 1秒 / 1 second
    
    // 最大允许的方块放置速率（每秒）
    // Maximum allowed block placement rate (per second)
    private static final double MAX_PLACE_RATE = 20.0;
    
    // 检测异常模式所需的最小放置次数
    // Minimum number of placements required to detect abnormal patterns
    private static final int MIN_PLACEMENTS_FOR_ANALYSIS = 5;
    
    public ScaffoldCheck() {
        super("Scaffold", "检测自动搭路作弊", true, 10);
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
        
        // 记录一次方块放置
        // Record a block placement
        long currentTime = System.currentTimeMillis();
        placeTimestamps.offer(currentTime);
        
        // 移除窗口外的旧放置记录
        // Remove old placement records outside the window
        while (!placeTimestamps.isEmpty() && (currentTime - placeTimestamps.peek()) > WINDOW_SIZE) {
            placeTimestamps.poll();
        }
        
        // 如果放置次数不足，不进行分析
        // If not enough placements, don't analyze
        if (placeTimestamps.size() < MIN_PLACEMENTS_FOR_ANALYSIS) {
            return false;
        }
        
        // 计算放置速率（每秒放置次数）
        // Calculate placement rate (placements per second)
        double placeRate = calculatePlaceRate();
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        double maxAllowedRate = MAX_PLACE_RATE * thresholdMultiplier;
        
        // 检查是否超出正常范围
        // Check if out of normal range
        boolean isTooFast = placeRate > maxAllowedRate;
        
        // 检查放置间隔的一致性（自动搭路通常有非常一致的间隔）
        // Check consistency of placement intervals (auto scaffold usually has very consistent intervals)
        boolean isTooConsistent = false;
        if (placeTimestamps.size() >= 10) { // 需要至少10次放置来分析一致性 / Need at least 10 placements to analyze consistency
            isTooConsistent = checkIntervalConsistency();
        }
        
        // 如果放置速率过高或放置间隔过于一致，则可能是自动搭路
        // If placement rate is too high or placement intervals are too consistent, may be auto scaffold
        return isTooFast || isTooConsistent;
    }
    
    /**
     * 计算方块放置速率
     * Calculate block placement rate
     */
    private double calculatePlaceRate() {
        if (placeTimestamps.size() < 2) {
            return 0;
        }
        
        long firstTimestamp = placeTimestamps.peek();
        long lastTimestamp = ((LinkedList<Long>) placeTimestamps).getLast();
        long timeWindow = lastTimestamp - firstTimestamp;
        
        if (timeWindow <= 0) {
            return 0;
        }
        
        // 计算每秒放置次数
        // Calculate placements per second
        return (placeTimestamps.size() * 1000.0) / timeWindow;
    }
    
    /**
     * 检查放置间隔一致性
     * Check placement interval consistency
     */
    private boolean checkIntervalConsistency() {
        if (placeTimestamps.size() < 3) {
            return false;
        }
        
        // 计算所有放置间隔
        // Calculate all placement intervals
        long[] intervals = new long[placeTimestamps.size() - 1];
        Long[] timestamps = placeTimestamps.toArray(new Long[0]);
        
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
        
        // 如果标准差非常小，说明间隔非常一致，可能是自动搭路
        // If standard deviation is very small, intervals are very consistent, may be auto scaffold
        // 正常玩家的放置间隔通常会有较大的变化
        // Normal player's placement intervals usually have larger variations
        return standardDeviation < 50; // 小于50毫秒的标准差被认为是过于一致的 / Standard deviation less than 50ms is considered too consistent
    }
}