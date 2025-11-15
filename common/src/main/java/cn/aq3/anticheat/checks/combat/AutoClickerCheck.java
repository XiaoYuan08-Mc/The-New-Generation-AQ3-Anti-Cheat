package cn.aq3.anticheat.checks.combat;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 自动点击器检查 - 检测异常的点击模式
 * Auto Clicker Check - Detect abnormal clicking patterns
 */
public class AutoClickerCheck extends Check {
    // 存储点击时间戳的队列
    // Queue to store click timestamps
    private final Queue<Long> clickTimestamps = new LinkedList<>();
    
    // 分析窗口大小（毫秒）
    // Analysis window size (milliseconds)
    private static final long WINDOW_SIZE = 1000; // 1秒 / 1 second
    
    // 最大允许的CPS（每秒点击次数）
    // Maximum allowed CPS (clicks per second)
    private static final double MAX_CPS = 20.0;
    
    // 检测异常模式所需的最小点击数
    // Minimum number of clicks required to detect abnormal patterns
    private static final int MIN_CLICKS_FOR_ANALYSIS = 5;
    
    public AutoClickerCheck() {
        super("AutoClicker", "检测自动点击器作弊", true, 10);
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
        
        // 记录一次点击
        // Record a click
        long currentTime = System.currentTimeMillis();
        clickTimestamps.offer(currentTime);
        
        // 移除窗口外的旧点击记录
        // Remove old click records outside the window
        while (!clickTimestamps.isEmpty() && (currentTime - clickTimestamps.peek()) > WINDOW_SIZE) {
            clickTimestamps.poll();
        }
        
        // 如果点击次数不足，不进行分析
        // If not enough clicks, don't analyze
        if (clickTimestamps.size() < MIN_CLICKS_FOR_ANALYSIS) {
            return false;
        }
        
        // 计算CPS（每秒点击次数）
        // Calculate CPS (clicks per second)
        double cps = calculateCPS();
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        double maxAllowedCPS = MAX_CPS * thresholdMultiplier;
        
        // 检查是否超出正常范围
        // Check if out of normal range
        boolean isTooFast = cps > maxAllowedCPS;
        
        // 检查点击间隔的一致性（自动点击器通常有非常一致的间隔）
        // Check consistency of click intervals (auto clickers usually have very consistent intervals)
        boolean isTooConsistent = false;
        if (clickTimestamps.size() >= 10) { // 需要至少10次点击来分析一致性 / Need at least 10 clicks to analyze consistency
            isTooConsistent = checkIntervalConsistency();
        }
        
        // 如果CPS过高或点击间隔过于一致，则可能是自动点击器
        // If CPS is too high or click intervals are too consistent, may be auto clicker
        return isTooFast || isTooConsistent;
    }
    
    /**
     * 计算CPS（每秒点击次数）
     * Calculate CPS (clicks per second)
     */
    private double calculateCPS() {
        if (clickTimestamps.size() < 2) {
            return 0;
        }
        
        long firstClick = clickTimestamps.peek();
        long lastClick = ((LinkedList<Long>) clickTimestamps).getLast();
        long timeWindow = lastClick - firstClick;
        
        // 避免除以零
        // Avoid division by zero
        if (timeWindow <= 0) {
            return 0;
        }
        
        // 计算CPS
        // Calculate CPS
        return (clickTimestamps.size() * 1000.0) / timeWindow;
    }
    
    /**
     * 检查点击间隔的一致性
     * Check consistency of click intervals
     */
    private boolean checkIntervalConsistency() {
        if (clickTimestamps.size() < 3) {
            return false;
        }
        
        // 计算所有点击间隔
        // Calculate all click intervals
        long[] intervals = new long[clickTimestamps.size() - 1];
        Long[] timestamps = clickTimestamps.toArray(new Long[0]);
        
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
        
        // 如果标准差非常小，说明间隔非常一致，可能是自动点击器
        // If standard deviation is very small, intervals are very consistent, may be auto clicker
        // 正常玩家的点击间隔通常会有较大的变化
        // Normal player's click intervals usually have larger variations
        return standardDeviation < 10; // 小于10毫秒的标准差被认为是过于一致的 / Standard deviation less than 10ms is considered too consistent
    }
}