package cn.aq3.anticheat.hwid;

import cn.aq3.anticheat.AQ3API;
import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 硬件标识管理器
 * Hardware Identification Manager
 */
public class HWIDManager {
    private final Map<String, Set<String>> hwidToPlayers = new HashMap<>();
    private final Map<String, String> playerToHWID = new HashMap<>();
    private final Set<String> bannedHWIDs = new HashSet<>();
    private final String hwidDataFile;
    
    public HWIDManager(String dataFile) {
        this.hwidDataFile = dataFile;
        loadData();
    }
    
    /**
     * 获取玩家的硬件标识
     * Get player's hardware identifier
     */
    public String getPlayerHWID(String playerName) {
        return playerToHWID.get(playerName);
    }
    
    /**
     * 生成硬件标识
     * Generate hardware identifier
     */
    public String generateHWID() {
        try {
            StringBuilder sb = new StringBuilder();
            
            // 获取系统属性
            sb.append(System.getProperty("os.name"));
            sb.append(System.getProperty("os.arch"));
            sb.append(System.getProperty("os.version"));
            
            // 获取网络接口信息
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface ni = networkInterfaces.nextElement();
                    byte[] hardwareAddress = ni.getHardwareAddress();
                    if (hardwareAddress != null) {
                        for (byte b : hardwareAddress) {
                            sb.append(String.format("%02X", b));
                        }
                    }
                }
            } catch (SocketException e) {
                // 如果无法获取网络接口信息，则跳过
            }
            
            // 获取系统环境变量
            sb.append(System.getenv("PROCESSOR_IDENTIFIER"));
            sb.append(System.getenv("COMPUTERNAME"));
            
            // 生成MD5哈希作为硬件标识
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sb.toString().getBytes());
            byte[] digest = md.digest();
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果MD5不可用，则使用简单哈希
            return Integer.toHexString(Objects.hash(
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"),
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("COMPUTERNAME")
            ));
        }
    }
    
    /**
     * 注册玩家的硬件标识
     * Register player's hardware identifier
     */
    public void registerPlayerHWID(String playerName, String hwid) {
        playerToHWID.put(playerName, hwid);
        hwidToPlayers.computeIfAbsent(hwid, k -> new HashSet<>()).add(playerName);
        saveData();
    }
    
    /**
     * 检查硬件标识是否被封禁
     * Check if hardware identifier is banned
     */
    public boolean isHWIDBanned(String hwid) {
        return bannedHWIDs.contains(hwid);
    }
    
    /**
     * 封禁硬件标识
     * Ban hardware identifier
     */
    public void banHWID(String hwid) {
        bannedHWIDs.add(hwid);
        saveData();
    }
    
    /**
     * 解封硬件标识
     * Unban hardware identifier
     */
    public void unbanHWID(String hwid) {
        bannedHWIDs.remove(hwid);
        saveData();
    }
    
    /**
     * 获取使用相同硬件标识的玩家列表
     * Get list of players using the same hardware identifier
     */
    public Set<String> getPlayersWithHWID(String hwid) {
        return new HashSet<>(hwidToPlayers.getOrDefault(hwid, new HashSet<>()));
    }
    
    /**
     * 加载数据
     * Load data
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(hwidDataFile))) {
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            
            if (data.containsKey("hwidToPlayers")) {
                hwidToPlayers.putAll((Map<String, Set<String>>) data.get("hwidToPlayers"));
            }
            
            if (data.containsKey("playerToHWID")) {
                playerToHWID.putAll((Map<String, String>) data.get("playerToHWID"));
            }
            
            if (data.containsKey("bannedHWIDs")) {
                bannedHWIDs.addAll((Set<String>) data.get("bannedHWIDs"));
            }
        } catch (IOException | ClassNotFoundException e) {
            // 文件不存在或损坏，使用默认空数据
            AQ3API.getInstance().getCheatLogger().logEvent("HWID data file not found or corrupted, using empty data");
        }
    }
    
    /**
     * 保存数据
     * Save data
     */
    public void saveData() {
        try {
            // 确保目录存在
            File file = new File(hwidDataFile);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(hwidDataFile))) {
                Map<String, Object> data = new HashMap<>();
                data.put("hwidToPlayers", hwidToPlayers);
                data.put("playerToHWID", playerToHWID);
                data.put("bannedHWIDs", bannedHWIDs);
                oos.writeObject(data);
            }
        } catch (IOException e) {
            AQ3API.getInstance().getCheatLogger().logEvent("Failed to save HWID data: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有被封禁的硬件标识
     * Get all banned hardware identifiers
     */
    public Set<String> getBannedHWIDs() {
        return new HashSet<>(bannedHWIDs);
    }
}