package cn.aq3.anticheat.logging;

import cn.aq3.anticheat.checks.Check;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 作弊日志记录器
 * Cheat logger
 */
public class CheatLogger {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String logFilePath;
    private PrintWriter writer;
    
    public CheatLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        try {
            this.writer = new PrintWriter(new FileWriter(logFilePath, true));
        } catch (IOException e) {
            System.err.println("Failed to initialize cheat logger: " + e.getMessage());
        }
    }
    
    /**
     * 记录作弊行为
     * Log cheat behavior
     */
    public void logCheat(UUID playerUUID, String playerName, Check check, String details) {
        if (writer == null) return;
        
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] Player %s (%s) failed %s check: %s", 
                                       timestamp, playerName, playerUUID, check.getName(), details);
        
        writer.println(logEntry);
        writer.flush();
    }
    
    /**
     * 记录系统事件
     * Log system event
     */
    public void logEvent(String event) {
        if (writer == null) return;
        
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] [SYSTEM] %s", timestamp, event);
        
        writer.println(logEntry);
        writer.flush();
    }
    
    /**
     * 关闭日志记录器
     * Close logger
     */
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}