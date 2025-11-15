package cn.aq3.anticheat.checks.movement;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 计时器检查 - 检测游戏加速作弊
 * Timer check - Detect game acceleration cheating
 */
public class TimerCheck extends Check {
    // 正常情况下，每秒应该大约有20个数据包
    // Under normal conditions, there should be about 20 packets per second
    private static final double EXPECTED_PACKETS_PER_SECOND = 20.0;
    
    // 允许的误差范围
    // Allowed margin of error
    private static final double TOLERANCE = 0.1;
    
    public TimerCheck() {
        super("Timer", "检测计时器作弊", true, 10);
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
        
        // 计算数据包频率
        // Calculate packet frequency
        long currentTime = System.currentTimeMillis();
        long lastPacketTime = playerData.getLastPacketTime();
        long timeDelta = currentTime - lastPacketTime;
        
        // 如果时间差为0，跳过检查
        // Skip check if time delta is 0
        if (timeDelta <= 0) {
            return false;
        }
        
        // 计算每秒数据包数
        // Calculate packets per second
        double packetsPerSecond = 1000.0 / timeDelta;
        
        // 根据灵敏度调整阈值
        // Adjust threshold based on sensitivity
        double thresholdMultiplier = 1.0 + (3 - sensitivity) * 0.2; // 灵敏度越低，阈值越宽松 / Lower sensitivity means more lenient threshold
        double maxAllowedPackets = EXPECTED_PACKETS_PER_SECOND * (1.0 + TOLERANCE * thresholdMultiplier);
        double minAllowedPackets = EXPECTED_PACKETS_PER_SECOND * (1.0 - TOLERANCE * thresholdMultiplier);
        
        // 检查是否超出正常范围
        // Check if out of normal range
        boolean isTooFast = packetsPerSecond > maxAllowedPackets;
        boolean isTooSlow = packetsPerSecond < minAllowedPackets;
        
        // 更新最后数据包时间
        // Update last packet time
        playerData.setLastPacketTime(currentTime);
        
        // 如果数据包频率异常，则可能是计时器作弊
        // If packet frequency is abnormal, may be timer cheating
        return isTooFast || isTooSlow;
    }
}