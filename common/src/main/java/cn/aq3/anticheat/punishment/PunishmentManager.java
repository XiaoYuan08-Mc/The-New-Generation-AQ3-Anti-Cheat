package cn.aq3.anticheat.punishment;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.violations.ViolationManager;

import java.util.UUID;

/**
 * 处罚管理器
 * Punishment manager
 */
public class PunishmentManager {
    private final int kickThreshold;
    private final int banThreshold;
    
    public PunishmentManager(int kickThreshold, int banThreshold) {
        this.kickThreshold = kickThreshold;
        this.banThreshold = banThreshold;
    }
    
    /**
     * 处理违规行为
     * Handle violation
     */
    public void handleViolation(UUID playerUUID, String playerName, Check check) {
        // 检查玩家是否免疫
        // Check if player is immune
        if (AQ3API.getInstance().getConfigManager().isImmune(playerName)) {
            // 免疫玩家不被处罚
            // Immune players are not punished
            return;
        }
        
        ViolationManager violationManager = AQ3API.getInstance().getViolationManager();
        
        // 获取总违规次数
        // Get total violation count
        int totalViolations = violationManager.getTotalViolations(playerUUID);
        
        // 检查是否启用发现作弊立即封禁
        // Check if ban on detection is enabled
        boolean banOnDetection = AQ3API.getInstance().getConfigManager()
            .getBoolean("punishments.ban_on_detection", true);
        
        if (banOnDetection) {
            // 立即封禁
            // Ban immediately
            banPlayer(playerUUID, playerName, check);
        } else {
            // 根据违规次数执行相应处罚
            // Execute appropriate punishment based on violation count
            if (totalViolations >= banThreshold) {
                banPlayer(playerUUID, playerName, check);
            } else if (totalViolations >= kickThreshold) {
                kickPlayer(playerUUID, playerName, check);
            } else {
                // 记录日志但不执行处罚
                // Log violation but don't execute punishment
                logViolation(playerUUID, playerName, check, totalViolations);
            }
        }
    }
    
    /**
     * 踢出玩家
     * Kick player
     */
    private void kickPlayer(UUID playerUUID, String playerName, Check check) {
        // 记录日志
        // Log action
        AQ3API.getInstance().getCheatLogger().logCheat(
            playerUUID, playerName, check, "Player kicked for cheating");
        
        // 实际踢出玩家
        // Actually kick the player
        if (AQ3API.getInstance().getBukkitAPI() != null) {
            try {
                // 通过反射调用Bukkit API的kickPlayer方法
                // Call Bukkit API kickPlayer method via reflection
                AQ3API.getInstance().getBukkitAPI().getClass()
                    .getMethod("kickPlayer", UUID.class, String.class, Check.class)
                    .invoke(AQ3API.getInstance().getBukkitAPI(), playerUUID, playerName, check);
            } catch (Exception e) {
                System.err.println("Failed to kick player: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 封禁玩家
     * Ban player
     */
    private void banPlayer(UUID playerUUID, String playerName, Check check) {
        // 获取封禁原因
        // Get ban reason
        String banReason = AQ3API.getInstance().getConfigManager()
            .getString("punishments.ban_reason", "[AQAC] 作弊行为检测");
        
        // 记录日志
        // Log action
        AQ3API.getInstance().getCheatLogger().logCheat(
            playerUUID, playerName, check, "Player banned for cheating: " + banReason);
        
        // 广播封禁消息
        // Broadcast ban message
        boolean broadcastBan = AQ3API.getInstance().getConfigManager()
            .getBoolean("punishments.broadcast_ban", true);
        
        if (broadcastBan) {
            System.out.println("[AQAC] 玩家 " + playerName + " 因作弊被封禁: " + banReason);
        }
        
        // 封禁玩家的硬件标识
        // Ban player's hardware identifier
        String playerHWID = AQ3API.getInstance().getHWIDManager().getPlayerHWID(playerName);
        if (playerHWID != null) {
            AQ3API.getInstance().getHWIDManager().banHWID(playerHWID);
            
            // 记录硬件标识封禁日志
            // Log hardware identifier ban
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Banned HWID " + playerHWID + " associated with player " + playerName);
            
            // 获取使用相同硬件标识的其他玩家
            // Get other players using the same hardware identifier
            java.util.Set<String> associatedPlayers = AQ3API.getInstance().getHWIDManager().getPlayersWithHWID(playerHWID);
            if (associatedPlayers.size() > 1) {
                // 记录关联账户信息
                // Log associated accounts information
                AQ3API.getInstance().getCheatLogger().logEvent(
                    "Found associated accounts for HWID " + playerHWID + ": " + associatedPlayers);
            }
        }
        
        // 实际封禁玩家
        // Actually ban the player
        if (AQ3API.getInstance().getBukkitAPI() != null) {
            try {
                // 通过反射调用Bukkit API的banPlayer方法
                // Call Bukkit API banPlayer method via reflection
                AQ3API.getInstance().getBukkitAPI().getClass()
                    .getMethod("banPlayer", UUID.class, String.class, Check.class, String.class)
                    .invoke(AQ3API.getInstance().getBukkitAPI(), playerUUID, playerName, check, banReason);
            } catch (Exception e) {
                System.err.println("Failed to ban player: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 记录违规日志
     * Log violation
     */
    private void logViolation(UUID playerUUID, String playerName, Check check, int totalViolations) {
        // 记录日志
        // Log action
        AQ3API.getInstance().getCheatLogger().logCheat(
            playerUUID, playerName, check, "Violation detected (Total: " + totalViolations + ")");
    }
    
    /**
     * 获取踢出阈值
     * Get kick threshold
     */
    public int getKickThreshold() {
        return kickThreshold;
    }
    
    /**
     * 获取封禁阈值
     * Get ban threshold
     */
    public int getBanThreshold() {
        return banThreshold;
    }
}