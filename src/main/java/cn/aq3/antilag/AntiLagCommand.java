package cn.aq3.antilag;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AntiLagCommand implements CommandExecutor {
    
    private final AntiLagPlugin plugin;
    
    public AntiLagCommand(AntiLagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.YELLOW + "AntiLag Plugin v1.0");
            sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + "Commands:");
            sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + "/antilag reload - Reload the configuration");
            sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + "/antilag stats - Show optimization statistics");
            sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + "/antilag clear - Clear ground items now");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                new ConfigManager(plugin).loadConfig();
                sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + "Configuration reloaded!");
                return true;
                
            case "stats":
                // TODO: Implement statistics display
                sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + "Statistics feature coming soon!");
                return true;
                
            case "clear":
                int removed = ItemCleanupManager.cleanupItems();
                sender.sendMessage(ChatColor.GREEN + "[AntiLag] " + ChatColor.WHITE + 
                    "Removed " + removed + " ground items!");
                return true;
                
            default:
                sender.sendMessage(ChatColor.RED + "[AntiLag] " + ChatColor.WHITE + "Unknown subcommand. Use /antilag for help.");
                return true;
        }
    }
}