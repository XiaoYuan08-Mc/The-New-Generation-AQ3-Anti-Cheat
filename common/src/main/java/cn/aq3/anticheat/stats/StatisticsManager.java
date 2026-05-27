package cn.aq3.anticheat.stats;

import cn.aq3.anticheat.checks.Check;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsManager {
    
    private final Map<String, AtomicInteger> checkViolations = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> playerViolations = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> checkTimes = new ConcurrentHashMap<>();
    
    private final AtomicInteger totalViolations = new AtomicInteger(0);
    private final AtomicInteger totalPlayersChecked = new AtomicInteger(0);
    private final AtomicInteger totalCheatersDetected = new AtomicInteger(0);
    private final AtomicLong totalCheckTime = new AtomicLong(0);
    
    private final List<ViolationRecord> recentViolations = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_RECENT_VIOLATIONS = 100;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void recordViolation(String checkName, String playerName) {
        checkViolations.computeIfAbsent(checkName, k -> new AtomicInteger(0)).incrementAndGet();
        playerViolations.computeIfAbsent(playerName, k -> new AtomicInteger(0)).incrementAndGet();
        totalViolations.incrementAndGet();
        
        recentViolations.add(0, new ViolationRecord(checkName, playerName, LocalDateTime.now()));
        while (recentViolations.size() > MAX_RECENT_VIOLATIONS) {
            recentViolations.remove(recentViolations.size() - 1);
        }
    }
    
    public void recordCheaterDetected(String playerName) {
        totalCheatersDetected.incrementAndGet();
    }
    
    public void recordPlayerChecked() {
        totalPlayersChecked.incrementAndGet();
    }
    
    public void recordCheckTime(String checkName, long timeNanos) {
        checkTimes.computeIfAbsent(checkName, k -> new AtomicLong(0)).addAndGet(timeNanos);
        totalCheckTime.addAndGet(timeNanos);
    }
    
    public StatisticsSnapshot getSnapshot() {
        Map<String, Integer> violationsCopy = new HashMap<>();
        checkViolations.forEach((k, v) -> violationsCopy.put(k, v.get()));
        
        Map<String, Integer> playerViolationsCopy = new HashMap<>();
        playerViolations.forEach((k, v) -> playerViolationsCopy.put(k, v.get()));
        
        List<ViolationRecord> recentCopy;
        synchronized (recentViolations) {
            recentCopy = new ArrayList<>(recentViolations);
        }
        
        return new StatisticsSnapshot(
            totalViolations.get(),
            totalPlayersChecked.get(),
            totalCheatersDetected.get(),
            totalCheckTime.get(),
            violationsCopy,
            playerViolationsCopy,
            recentCopy
        );
    }
    
    public void reset() {
        checkViolations.clear();
        playerViolations.clear();
        checkTimes.clear();
        totalViolations.set(0);
        totalPlayersChecked.set(0);
        totalCheatersDetected.set(0);
        totalCheckTime.set(0);
        recentViolations.clear();
    }
    
    public static class StatisticsSnapshot {
        public final int totalViolations;
        public final int totalPlayersChecked;
        public final int totalCheatersDetected;
        public final long totalCheckTimeNanos;
        public final Map<String, Integer> violationsByCheck;
        public final Map<String, Integer> violationsByPlayer;
        public final List<ViolationRecord> recentViolations;
        
        public StatisticsSnapshot(int totalViolations, int totalPlayersChecked, int totalCheatersDetected,
                                 long totalCheckTimeNanos, Map<String, Integer> violationsByCheck,
                                 Map<String, Integer> violationsByPlayer, List<ViolationRecord> recentViolations) {
            this.totalViolations = totalViolations;
            this.totalPlayersChecked = totalPlayersChecked;
            this.totalCheatersDetected = totalCheatersDetected;
            this.totalCheckTimeNanos = totalCheckTimeNanos;
            this.violationsByCheck = violationsByCheck;
            this.violationsByPlayer = violationsByPlayer;
            this.recentViolations = recentViolations;
        }
    }
    
    public static class ViolationRecord {
        public final String checkName;
        public final String playerName;
        public final LocalDateTime timestamp;
        
        public ViolationRecord(String checkName, String playerName, LocalDateTime timestamp) {
            this.checkName = checkName;
            this.playerName = playerName;
            this.timestamp = timestamp;
        }
        
        public String getFormattedTime() {
            return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
