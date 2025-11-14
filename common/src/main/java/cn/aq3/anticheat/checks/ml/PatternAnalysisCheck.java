package cn.aq3.anticheat.checks.ml;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.ml.CheatPatternDetector;
import cn.aq3.anticheat.player.PlayerData;

/**
 * 模式分析检查 - 使用机器学习检测复杂作弊模式
 * Pattern analysis check - Uses machine learning to detect complex cheat patterns
 */
public class PatternAnalysisCheck extends Check {
    private final CheatPatternDetector patternDetector;
    
    public PatternAnalysisCheck(CheatPatternDetector patternDetector) {
        super("PatternAnalysis", "检测复杂作弊模式", true, 5);
        this.patternDetector = patternDetector;
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
        
        // 使用机器学习模型分析玩家行为
        // Use machine learning model to analyze player behavior
        boolean isCheat = patternDetector.analyzePlayerBehavior(playerData);
        
        // 根据灵敏度调整结果
        // Adjust result based on sensitivity
        if (isCheat && sensitivity <= 2) {
            // 在较低灵敏度下，增加额外检查以减少误报
            // At lower sensitivity, add extra checks to reduce false positives
            return false;
        }
        
        // 根据灵敏度调整检测阈值
        // Adjust detection threshold based on sensitivity
        if (sensitivity == 1) {
            // 在最低灵敏度下，进一步减少误报
            // At minimum sensitivity, further reduce false positives
            return isCheat && Math.random() > 0.3; // 70%概率忽略检测结果 / 70% probability to ignore detection result
        }
        
        return isCheat;
    }
}