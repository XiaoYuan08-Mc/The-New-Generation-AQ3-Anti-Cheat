package cn.aq3.anticheat.commands;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.hwid.HWIDManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 硬件标识命令处理器
 * Hardware Identifier Command Handler
 */
public class HWIDCommand {
    
    /**
     * 处理HWID相关命令
     * Handle HWID-related commands
     */
    public static void handleHWIDCommand(String[] args) {
        if (args.length < 2) {
            System.out.println("用法: /aq3 hwid <子命令>");
            System.out.println("可用子命令:");
            System.out.println("  list - 列出所有被封禁的硬件标识");
            System.out.println("  ban <hwid> - 封禁指定的硬件标识");
            System.out.println("  unban <hwid> - 解封指定的硬件标识");
            System.out.println("  check <player> - 检查玩家的硬件标识");
            System.out.println("  info <hwid> - 显示硬件标识的详细信息");
            return;
        }
        
        HWIDManager hwidManager = AQ3API.getInstance().getHWIDManager();
        String subCommand = args[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
                handleListCommand(hwidManager);
                break;
                
            case "ban":
                if (args.length < 3) {
                    System.out.println("用法: /aq3 hwid ban <hwid>");
                    return;
                }
                handleBanCommand(hwidManager, args[2]);
                break;
                
            case "unban":
                if (args.length < 3) {
                    System.out.println("用法: /aq3 hwid unban <hwid>");
                    return;
                }
                handleUnbanCommand(hwidManager, args[2]);
                break;
                
            case "check":
                if (args.length < 3) {
                    System.out.println("用法: /aq3 hwid check <player>");
                    return;
                }
                handleCheckCommand(hwidManager, args[2]);
                break;
                
            case "info":
                if (args.length < 3) {
                    System.out.println("用法: /aq3 hwid info <hwid>");
                    return;
                }
                handleInfoCommand(hwidManager, args[2]);
                break;
                
            default:
                System.out.println("未知的子命令: " + subCommand);
                System.out.println("使用 '/aq3 hwid' 查看帮助");
        }
    }
    
    /**
     * 处理列出封禁的硬件标识命令
     * Handle list banned HWIDs command
     */
    private static void handleListCommand(@NotNull HWIDManager hwidManager) {
        Set<String> bannedHWIDs = hwidManager.getBannedHWIDs();
        
        if (bannedHWIDs.isEmpty()) {
            System.out.println("[AQAC] 没有被封禁的硬件标识");
            return;
        }
        
        System.out.println("[AQAC] 被封禁的硬件标识列表:");
        for (String hwid : bannedHWIDs) {
            System.out.println("  " + hwid);
        }
        System.out.println("总计: " + bannedHWIDs.size() + " 个被封禁的硬件标识");
    }
    
    /**
     * 处理封禁硬件标识命令
     * Handle ban HWID command
     */
    private static void handleBanCommand(@NotNull HWIDManager hwidManager, String hwid) {
        if (hwidManager.isHWIDBanned(hwid)) {
            System.out.println("[AQAC] 硬件标识 " + hwid + " 已经被封禁");
            return;
        }
        
        hwidManager.banHWID(hwid);
        System.out.println("[AQAC] 成功封禁硬件标识: " + hwid);
    }
    
    /**
     * 处理解封硬件标识命令
     * Handle unban HWID command
     */
    private static void handleUnbanCommand(@NotNull HWIDManager hwidManager, String hwid) {
        if (!hwidManager.isHWIDBanned(hwid)) {
            System.out.println("[AQAC] 硬件标识 " + hwid + " 未被封禁");
            return;
        }
        
        hwidManager.unbanHWID(hwid);
        System.out.println("[AQAC] 成功解封硬件标识: " + hwid);
    }
    
    /**
     * 处理检查玩家硬件标识命令
     * Handle check player HWID command
     */
    private static void handleCheckCommand(@NotNull HWIDManager hwidManager, String playerName) {
        String hwid = hwidManager.getPlayerHWID(playerName);
        
        if (hwid == null) {
            System.out.println("[AQAC] 未找到玩家 " + playerName + " 的硬件标识信息");
            return;
        }
        
        boolean isBanned = hwidManager.isHWIDBanned(hwid);
        System.out.println("[AQAC] 玩家 " + playerName + " 的硬件标识信息:");
        System.out.println("  硬件标识: " + hwid);
        System.out.println("  状态: " + (isBanned ? "已封禁" : "正常"));
        
        if (isBanned) {
            System.out.println("  注意: 此硬件标识已被封禁，玩家将无法加入服务器");
        }
    }
    
    /**
     * 处理显示硬件标识详细信息命令
     * Handle show HWID info command
     */
    private static void handleInfoCommand(@NotNull HWIDManager hwidManager, String hwid) {
        boolean isBanned = hwidManager.isHWIDBanned(hwid);
        Set<String> associatedPlayers = hwidManager.getPlayersWithHWID(hwid);
        
        System.out.println("[AQAC] 硬件标识 " + hwid + " 的详细信息:");
        System.out.println("  状态: " + (isBanned ? "已封禁" : "正常"));
        System.out.println("  关联账户数量: " + associatedPlayers.size());
        
        if (!associatedPlayers.isEmpty()) {
            System.out.println("  关联账户:");
            for (String player : associatedPlayers) {
                System.out.println("    - " + player);
            }
        }
    }
}