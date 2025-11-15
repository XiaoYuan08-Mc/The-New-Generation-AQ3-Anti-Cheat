package cn.aq3.anticheat.bukkit;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.commands.HWIDCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * AQ3反作弊命令处理器
 * AQ3 Anti-Cheat Command Handler
 */
public class AQ3Command implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("[AQAC] AQ3反作弊系统 v1.0.0");
            sender.sendMessage("[AQAC] 使用 /aq3 help 查看帮助");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                // 重新加载配置
                // Reload configuration
                AQ3API.getInstance().getConfigManager().loadConfigFromFile(
                    new java.io.File("plugins/AQ3AntiCheat/config.yml"));
                AQ3API.getInstance().getConfigManager().loadAdminConfig(
                    new java.io.File("plugins/AQ3AntiCheat/admin-config.yml"));
                sender.sendMessage("[AQAC] 配置已重新加载");
                break;
                
            case "version":
                // 显示版本信息
                // Show version information
                sender.sendMessage("[AQAC] AQ3反作弊系统 v1.0.0");
                sender.sendMessage("[AQAC] 作者: AQ3DevTeam");
                sender.sendMessage("[AQAC] GitHub: https://github.com/XiaoYuan08-Mc");
                break;
                
            case "hwid":
                // 硬件标识相关命令
                // Hardware identifier related commands
                HWIDCommand.handleHWIDCommand(args);
                break;
                
            case "help":
            default:
                // 显示帮助信息
                // Show help information
                sender.sendMessage("[AQAC] AQ3反作弊系统命令帮助:");
                sender.sendMessage("  /aq3 reload - 重新加载配置");
                sender.sendMessage("  /aq3 version - 显示版本信息");
                sender.sendMessage("  /aq3 hwid - 硬件标识管理命令");
                sender.sendMessage("  /aq3 help - 显示此帮助信息");
                break;
        }
        
        return true;
    }
}