package cn.aq3.anticheat.bukkit.ban;

import cn.aq3.anticheat.AQ3API;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AdvancedBan集成处理器
 * AdvancedBan Integration Handler
 * 
 * 处理与AdvancedBan插件的集成，提供更强大的封禁功能
 * Handles integration with the AdvancedBan plugin, providing more powerful banning capabilities
 */
public class AdvancedBanHandler implements Listener {
    private Plugin plugin;
    private boolean advancedBanAvailable;
    private Object punishmentManager;
    private Method addPunishmentMethod;
    private Method isBannedMethod;
    private Method removePunishmentMethod;
    
    public AdvancedBanHandler(Plugin plugin) {
        this.plugin = plugin;
        this.advancedBanAvailable = checkAdvancedBanAvailability();
    }
    
    /**
     * 检查AdvancedBan是否可用
     * Check if AdvancedBan is available
     */
    private boolean checkAdvancedBanAvailability() {
        try {
            // 尝试获取AdvancedBan类
            Class<?> advancedBanClass = Class.forName("me.leoko.advancedban.bukkit.AdvancedBan");
            Class<?> punishmentManagerClass = Class.forName("me.leoko.advancedban.manager.PunishmentManager");
            
            // 获取单例实例
            Method getInstanceMethod = punishmentManagerClass.getMethod("get");
            punishmentManager = getInstanceMethod.invoke(null);
            
            // 获取方法
            addPunishmentMethod = punishmentManagerClass.getMethod("addPunishment", Map.class);
            isBannedMethod = punishmentManagerClass.getMethod("isBanned", String.class);
            removePunishmentMethod = punishmentManagerClass.getMethod("removePunishment", String.class, String.class);
            
            return Bukkit.getPluginManager().isPluginEnabled("AdvancedBan");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 封禁玩家
     * Ban player
     */
    public boolean banPlayer(UUID playerUUID, String playerName, String reason, String operator) {
        if (!advancedBanAvailable) {
            return false;
        }
        
        try {
            // 创建封禁参数
            Map<String, Object> punishmentData = new HashMap<>();
            punishmentData.put("name", playerName);
            punishmentData.put("uuid", playerUUID.toString());
            punishmentData.put("reason", reason);
            punishmentData.put("operator", operator);
            punishmentData.put("punishmentType", "BAN");
            punishmentData.put("start", System.currentTimeMillis());
            punishmentData.put("end", -1L); // 永久封禁
            
            // 使用AdvancedBan封禁玩家
            addPunishmentMethod.invoke(punishmentManager, punishmentData);
            
            // 如果玩家在线，踢出他们
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                player.kickPlayer(reason);
            }
            
            // 记录日志
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Banned player " + playerName + " using AdvancedBan: " + reason);
            
            return true;
        } catch (Exception e) {
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Failed to ban player " + playerName + " using AdvancedBan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 临时封禁玩家
     * Temporarily ban player
     */
    public boolean tempBanPlayer(UUID playerUUID, String playerName, String reason, String operator, long duration) {
        if (!advancedBanAvailable) {
            return false;
        }
        
        try {
            // 创建封禁参数
            Map<String, Object> punishmentData = new HashMap<>();
            punishmentData.put("name", playerName);
            punishmentData.put("uuid", playerUUID.toString());
            punishmentData.put("reason", reason);
            punishmentData.put("operator", operator);
            punishmentData.put("punishmentType", "TEMP_BAN");
            punishmentData.put("start", System.currentTimeMillis());
            punishmentData.put("end", System.currentTimeMillis() + duration);
            
            // 使用AdvancedBan临时封禁玩家
            addPunishmentMethod.invoke(punishmentManager, punishmentData);
            
            // 如果玩家在线，踢出他们
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                player.kickPlayer(reason);
            }
            
            // 记录日志
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Temporarily banned player " + playerName + " using AdvancedBan for " + duration + "ms: " + reason);
            
            return true;
        } catch (Exception e) {
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Failed to temporarily ban player " + playerName + " using AdvancedBan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 踢出玩家
     * Kick player
     */
    public boolean kickPlayer(UUID playerUUID, String playerName, String reason, String operator) {
        if (!advancedBanAvailable) {
            return false;
        }
        
        try {
            // 创建踢出参数
            Map<String, Object> punishmentData = new HashMap<>();
            punishmentData.put("name", playerName);
            punishmentData.put("uuid", playerUUID.toString());
            punishmentData.put("reason", reason);
            punishmentData.put("operator", operator);
            punishmentData.put("punishmentType", "KICK");
            punishmentData.put("start", System.currentTimeMillis());
            punishmentData.put("end", System.currentTimeMillis());
            
            // 使用AdvancedBan踢出玩家
            addPunishmentMethod.invoke(punishmentManager, punishmentData);
            
            // 如果玩家在线，踢出他们
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                player.kickPlayer(reason);
            }
            
            // 记录日志
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Kicked player " + playerName + " using AdvancedBan: " + reason);
            
            return true;
        } catch (Exception e) {
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Failed to kick player " + playerName + " using AdvancedBan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查玩家是否已被封禁
     * Check if player is banned
     */
    public boolean isPlayerBanned(UUID playerUUID, String playerName) {
        if (!advancedBanAvailable) {
            return false;
        }
        
        try {
            return (Boolean) isBannedMethod.invoke(punishmentManager, playerUUID.toString());
        } catch (Exception e) {
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Failed to check ban status for player " + playerName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 解除玩家封禁
     * Unban player
     */
    public boolean unbanPlayer(UUID playerUUID, String playerName, String operator) {
        if (!advancedBanAvailable) {
            return false;
        }
        
        try {
            // 使用AdvancedBan解除玩家封禁
            removePunishmentMethod.invoke(punishmentManager, playerUUID.toString(), "BAN");
            
            // 记录日志
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Unbanned player " + playerName + " using AdvancedBan by " + operator);
            
            return true;
        } catch (Exception e) {
            AQ3API.getInstance().getCheatLogger().logEvent(
                "Failed to unban player " + playerName + " using AdvancedBan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取玩家惩罚历史
     * Get player punishment history
     */
    public java.util.List<Map<String, Object>> getPlayerPunishments(UUID playerUUID, String playerName) {
        // 简化实现，实际应用中可能需要更复杂的逻辑
        return new java.util.ArrayList<>();
    }
    
    /**
     * 监听封禁事件
     * Listen for ban events
     */
    @EventHandler
    public void onPunishment(me.leoko.advancedban.bukkit.event.PunishmentEvent event) {
        // 可以在这里处理AdvancedBan的封禁事件
        // 可以用于同步封禁信息到AQ3系统
        
        // 记录日志
        AQ3API.getInstance().getCheatLogger().logEvent(
            "AdvancedBan punishment issued: " + event.getPunishment().getType() + 
            " to " + event.getPunishment().getName() + 
            " for " + event.getPunishment().getReason());
    }
    
    /**
     * 检查AdvancedBan是否可用
     * Check if AdvancedBan is available
     */
    public boolean isAdvancedBanAvailable() {
        return advancedBanAvailable;
    }
}