package cn.aq3.antilag;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OptimizeCommand implements CommandExecutor {
    
    private final AntiLagPlugin plugin;
    
    public OptimizeCommand(AntiLagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.YELLOW + "Optimization Commands:");
            sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.WHITE + "/opt reload - Reload the configuration");
            sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.WHITE + "/opt gc - Run garbage collection");
            sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.WHITE + "/opt clear - Clear ground items now");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                new ConfigManager(plugin).loadConfig();
                sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.WHITE + "Configuration reloaded!");
                return true;
                
            case "gc":
                System.gc();
                sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.WHITE + "Garbage collection initiated!");
                return true;
                
            case "clear":
                int removed = ItemCleanupManager.cleanupItems();
                sender.sendMessage(ChatColor.GREEN + "[Optimize] " + ChatColor.WHITE + 
                    "Removed " + removed + " ground items!");
                return true;
                
            default:
                sender.sendMessage(ChatColor.RED + "[Optimize] " + ChatColor.WHITE + "Unknown subcommand. Use /opt for help.");
                return true;
        }
    }
}