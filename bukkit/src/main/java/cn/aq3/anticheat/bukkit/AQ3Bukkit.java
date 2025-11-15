package cn.aq3.anticheat.bukkit;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.bukkit.ban.AdvancedBanHandler;
import cn.aq3.anticheat.bukkit.util.HackerDisplay;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * AQ3反作弊 Bukkit主类
 * AQ3 Anti-Cheat Bukkit Main Class
 * 
 * 新一代反作弊系统，具有以下特性：
 * - 精确的物理引擎模拟
 * - 世界复制系统
 * - 多种作弊检测
 * - 机器学习模式识别
 * - 可配置的处罚系统
 * - 完整的日志记录
 */
public class AQ3Bukkit extends JavaPlugin {
    private AdvancedBanHandler advancedBanHandler;
    
    @Override
    public void onLoad() {
        // 显示启动动画
        // Display startup animation
        HackerDisplay.showStartupAnimation();
        
        // 初始化API
        AQ3API.setInstance(new AQ3API());
        AQ3API.getInstance().setBukkitAPI(this);
    }
    
    @Override
    public void onEnable() {
        // 初始化API
        AQ3API.getInstance().initialize();
        
        // 初始化AdvancedBan处理器
        // Initialize AdvancedBan handler
        initializeAdvancedBanHandler();
        
        // 注册监听器
        registerListeners();
        
        // 注册命令
        registerCommands();
        
        getLogger().info("AQ3反作弊系统已启用 | Enabled AQ3 Anti-Cheat System");
    }
    
    @Override
    public void onDisable() {
        // 关闭API
        AQ3API.getInstance().shutdown();
        
        // 显示关闭动画
        // Display shutdown animation
        HackerDisplay.showShutdownAnimation();
        
        getLogger().info("AQ3反作弊系统已禁用 | Disabled AQ3 Anti-Cheat System");
    }
    
    /**
     * 初始化AdvancedBan处理器
     * Initialize AdvancedBan handler
     */
    private void initializeAdvancedBanHandler() {
        try {
            advancedBanHandler = new AdvancedBanHandler(this);
            
            // 注册事件监听器
            if (advancedBanHandler.isAdvancedBanAvailable()) {
                getServer().getPluginManager().registerEvents(advancedBanHandler, this);
                getLogger().info("AdvancedBan集成已启用 | AdvancedBan integration enabled");
            } else {
                getLogger().info("AdvancedBan未找到，使用内置封禁系统 | AdvancedBan not found, using built-in ban system");
            }
        } catch (Exception e) {
            getLogger().warning("无法初始化AdvancedBan处理器: " + e.getMessage());
            getLogger().warning("Using built-in ban system due to AdvancedBan initialization error");
        }
    }
    
    private void registerListeners() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    private void registerCommands() {
        // 注册命令
        this.getCommand("aq3").setExecutor(new AQ3Command());
    }
    
    /**
     * 踢出玩家
     * Kick player
     */
    public void kickPlayer(UUID playerUUID, String playerName, cn.aq3.anticheat.checks.Check check) {
        String kickMessage = AQ3API.getInstance().getConfigManager()
            .getString("punishments.kick_message", "[AQAC] 作弊行为检测");
        
        // 尝试使用AdvancedBan踢出玩家
        if (advancedBanHandler != null && advancedBanHandler.isAdvancedBanAvailable()) {
            advancedBanHandler.kickPlayer(playerUUID, playerName, kickMessage, "AQ3AntiCheat");
            return;
        }
        
        // 使用内置系统踢出玩家
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.kickPlayer(kickMessage);
        }
    }
    
    /**
     * 封禁玩家
     * Ban player
     */
    public void banPlayer(UUID playerUUID, String playerName, cn.aq3.anticheat.checks.Check check, String banReason) {
        // 检查是否启用硬件标识封禁
        // Check if hardware identifier banning is enabled
        boolean banHWID = AQ3API.getInstance().getConfigManager()
            .getBoolean("hwid.ban_hwid_on_ban", true);
            
        // 检查是否使用AdvancedBan
        boolean useAdvancedBan = AQ3API.getInstance().getConfigManager()
            .getBoolean("punishments.use_advancedban", true);
            
        // 检查封禁时长
        long banDuration = AQ3API.getInstance().getConfigManager()
            .getLong("punishments.advancedban_temp_ban_duration", 86400000L); // 默认24小时
            
        // 尝试使用AdvancedBan封禁玩家
        if (useAdvancedBan && advancedBanHandler != null && advancedBanHandler.isAdvancedBanAvailable()) {
            boolean success;
            if (banDuration > 0) {
                // 临时封禁
                success = advancedBanHandler.tempBanPlayer(playerUUID, playerName, banReason, "AQ3AntiCheat", banDuration);
            } else {
                // 永久封禁
                success = advancedBanHandler.banPlayer(playerUUID, playerName, banReason, "AQ3AntiCheat");
            }
            
            if (success) {
                // 封禁硬件标识
                if (banHWID) {
                    banPlayerHWID(playerName);
                }
                return;
            }
        }
        
        // 使用内置系统封禁玩家
        // 封禁玩家
        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, banReason, null, "AQ3 Anti-Cheat");
        
        // 如果玩家在线，踢出他们
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.kickPlayer(banReason);
        }
        
        // 封禁硬件标识
        if (banHWID) {
            banPlayerHWID(playerName);
        }
    }
    
    /**
     * 封禁玩家的硬件标识
     * Ban player's hardware identifier
     */
    private void banPlayerHWID(String playerName) {
        // 获取玩家的硬件标识
        // Get player's hardware identifier
        String playerHWID = AQ3API.getInstance().getHWIDManager().getPlayerHWID(playerName);
        if (playerHWID != null) {
            // 封禁硬件标识
            // Ban hardware identifier
            AQ3API.getInstance().getHWIDManager().banHWID(playerHWID);
            
            // 获取使用相同硬件标识的其他账户
            // Get other accounts using the same hardware identifier
            java.util.Set<String> associatedPlayers = AQ3API.getInstance().getHWIDManager()
                .getPlayersWithHWID(playerHWID);
            
            // 同时封禁这些账户
            // Also ban these accounts
            for (String associatedPlayer : associatedPlayers) {
                if (!associatedPlayer.equals(playerName)) {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(
                        associatedPlayer, 
                        "[AQAC] Associated account of banned player", 
                        null, 
                        "AQ3 Anti-Cheat"
                    );
                }
            }
        }
    }
    
    /**
     * 获取AdvancedBan处理器实例
     * Get AdvancedBan handler instance
     */
    public AdvancedBanHandler getAdvancedBanHandler() {
        return advancedBanHandler;
    }
}