package cn.aq3.anticheat.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityManager {
    
    private static final Pattern SAFE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");
    private static final Pattern SAFE_COMMAND_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-./ ]{1,256}$");
    private static final Pattern SAFE_IP_PATTERN = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");
    
    private final Set<String> allowedCommands = new HashSet<>();
    private final Set<String> allowedPlugins = new HashSet<>();
    private final Set<String> allowedIPs = new HashSet<>();
    
    private boolean enableCommandWhitelist = false;
    private boolean enableIPWhitelist = false;
    private boolean enableNameValidation = true;
    
    public SecurityManager() {
        allowedCommands.add("kick");
        allowedCommands.add("ban");
        allowedCommands.add("tempban");
        allowedCommands.add("mute");
        allowedCommands.add("warn");
    }
    
    public boolean isValidPlayerName(String name) {
        if (!enableNameValidation) {
            return true;
        }
        if (name == null || name.isEmpty()) {
            return false;
        }
        return SAFE_NAME_PATTERN.matcher(name).matches();
    }
    
    public boolean isValidCommand(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }
        if (!enableCommandWhitelist) {
            return SAFE_COMMAND_PATTERN.matcher(command).matches();
        }
        String cmdName = command.split(" ")[0].toLowerCase();
        return allowedCommands.contains(cmdName);
    }
    
    public boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        if (!enableIPWhitelist) {
            return SAFE_IP_PATTERN.matcher(ip).matches();
        }
        return allowedIPs.contains(ip);
    }
    
    public boolean isValidHWID(String hwid) {
        if (hwid == null || hwid.isEmpty()) {
            return false;
        }
        return hwid.matches("^[a-f0-9]{32}$");
    }
    
    public boolean isValidCheckName(String checkName) {
        if (checkName == null || checkName.isEmpty()) {
            return false;
        }
        return checkName.matches("^[a-zA-Z][a-zA-Z0-9]*$");
    }
    
    public String sanitizeCommand(String command) {
        if (command == null) {
            return "";
        }
        String sanitized = command.replaceAll("[<>\"';&|]", "");
        return sanitized.trim();
    }
    
    public String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[<>\"';&|]", "").trim();
    }
    
    public void addAllowedCommand(String command) {
        if (command != null && !command.isEmpty()) {
            allowedCommands.add(command.toLowerCase());
        }
    }
    
    public void removeAllowedCommand(String command) {
        if (command != null) {
            allowedCommands.remove(command.toLowerCase());
        }
    }
    
    public void addAllowedPlugin(String pluginName) {
        if (pluginName != null && !pluginName.isEmpty()) {
            allowedPlugins.add(pluginName.toLowerCase());
        }
    }
    
    public void addAllowedIP(String ip) {
        if (isValidIP(ip)) {
            allowedIPs.add(ip);
        }
    }
    
    public void setEnableCommandWhitelist(boolean enable) {
        this.enableCommandWhitelist = enable;
    }
    
    public void setEnableIPWhitelist(boolean enable) {
        this.enableIPWhitelist = enable;
    }
    
    public void setEnableNameValidation(boolean enable) {
        this.enableNameValidation = enable;
    }
    
    public boolean isCommandAllowed(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }
        String cmdName = command.split(" ")[0].toLowerCase();
        return allowedCommands.contains(cmdName);
    }
    
    public boolean isPluginAllowed(String pluginName) {
        if (pluginName == null || pluginName.isEmpty()) {
            return false;
        }
        return allowedPlugins.contains(pluginName.toLowerCase());
    }
    
    public boolean isIPAllowed(String ip) {
        if (!enableIPWhitelist) {
            return true;
        }
        return allowedIPs.contains(ip);
    }
}
