package cn.aq3.anticheat.hwid;

import cn.aq3.anticheat.AQ3API;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class HWIDManager {
    private final Map<String, Set<String>> hwidToPlayers = new HashMap<>();
    private final Map<String, String> playerToHWID = new HashMap<>();
    private final Set<String> bannedHWIDs = new HashSet<>();
    private final String hwidDataFile;
    private final Yaml yaml;
    
    public HWIDManager(String dataFile) {
        this.hwidDataFile = dataFile;
        this.yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        loadData();
    }
    
    public String getPlayerHWID(String playerName) {
        return playerToHWID.get(playerName);
    }
    
    public String generateHWID() {
        try {
            StringBuilder sb = new StringBuilder();
            
            sb.append(System.getProperty("os.name"));
            sb.append(System.getProperty("os.arch"));
            sb.append(System.getProperty("os.version"));
            
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
            }
            
            sb.append(System.getenv("PROCESSOR_IDENTIFIER"));
            sb.append(System.getenv("COMPUTERNAME"));
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sb.toString().getBytes());
            byte[] digest = md.digest();
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(Objects.hash(
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"),
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("COMPUTERNAME")
            ));
        }
    }
    
    public void registerPlayerHWID(String playerName, String hwid) {
        playerToHWID.put(playerName, hwid);
        hwidToPlayers.computeIfAbsent(hwid, k -> new HashSet<>()).add(playerName);
        saveData();
    }
    
    public boolean isHWIDBanned(String hwid) {
        return bannedHWIDs.contains(hwid);
    }
    
    public void banHWID(String hwid) {
        bannedHWIDs.add(hwid);
        saveData();
    }
    
    public void unbanHWID(String hwid) {
        bannedHWIDs.remove(hwid);
        saveData();
    }
    
    public Set<String> getPlayersWithHWID(String hwid) {
        return new HashSet<>(hwidToPlayers.getOrDefault(hwid, new HashSet<>()));
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(hwidDataFile);
        File oldFile = new File(hwidDataFile.replace(".yml", ".dat"));
        
        if (oldFile.exists() && !file.exists()) {
            migrateFromOldFormat(oldFile);
        }
        
        if (!file.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            Map<String, Object> data = yaml.load(reader);
            
            if (data != null) {
                if (data.containsKey("hwidToPlayers")) {
                    Map<String, List<String>> raw = (Map<String, List<String>>) data.get("hwidToPlayers");
                    for (Map.Entry<String, List<String>> entry : raw.entrySet()) {
                        hwidToPlayers.put(entry.getKey(), new HashSet<>(entry.getValue()));
                    }
                }
                
                if (data.containsKey("playerToHWID")) {
                    playerToHWID.putAll((Map<String, String>) data.get("playerToHWID"));
                }
                
                if (data.containsKey("bannedHWIDs")) {
                    bannedHWIDs.addAll((List<String>) data.get("bannedHWIDs"));
                }
            }
        } catch (IOException e) {
            logError("Failed to load HWID data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void migrateFromOldFormat(File oldFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(oldFile))) {
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
            
            saveData();
            oldFile.delete();
            logError("Migrated HWID data from old format to YAML");
        } catch (IOException | ClassNotFoundException e) {
            logError("Failed to migrate HWID data from old format: " + e.getMessage());
        }
    }
    
    public void saveData() {
        try {
            File file = new File(hwidDataFile);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            Map<String, Object> data = new HashMap<>();
            Map<String, List<String>> hwidToList = new HashMap<>();
            for (Map.Entry<String, Set<String>> entry : hwidToPlayers.entrySet()) {
                hwidToList.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            
            data.put("hwidToPlayers", hwidToList);
            data.put("playerToHWID", playerToHWID);
            data.put("bannedHWIDs", new ArrayList<>(bannedHWIDs));
            
            try (Writer writer = new FileWriter(file)) {
                yaml.dump(data, writer);
            }
        } catch (IOException e) {
            logError("Failed to save HWID data: " + e.getMessage());
        }
    }
    
    private void logError(String message) {
        try {
            if (AQ3API.isInitialized()) {
                AQ3API.getInstance().getCheatLogger().logEvent(message);
            } else {
                System.err.println("[AQ3] " + message);
            }
        } catch (Exception e) {
            System.err.println("[AQ3] " + message);
        }
    }
    
    public Set<String> getBannedHWIDs() {
        return new HashSet<>(bannedHWIDs);
    }
}
