package cn.aq3.anticheat.bukkit.util;

import java.util.concurrent.TimeUnit;

/**
 * 黑客风格显示工具类
 * Hacker style display utility class
 */
public class HackerDisplay {
    
    /**
     * 显示启动动画
     * Display startup animation
     */
    public static void showStartupAnimation() {
        try {
            // 不再清屏，直接显示动画
            // No longer clear screen, display animation directly
            
            // 显示AQAC标题
            // Display AQAC title
            String[] title = {
                " █████╗  ██████╗  ",
                "██╔══██╗██╔═══██╗ ",
                "███████║██║   ██║ ",
                "██╔══██║██║▄▄ ██║ ",
                "██║  ██║╚██████╔╝ ",
                "╚═╝  ╚═╝ ╚══▀▀═╝  ",
                "新一代反作弊AQ3"
            };
            
            // 逐行显示标题
            // Display title line by line
            for (String line : title) {
                System.out.println("\033[32m" + line + "\033[0m"); // 绿色显示 / Green display
                Thread.sleep(200);
            }
            
            System.out.println();
            
            // 显示加载进度
            // Display loading progress
            String loading = "AQ3开始初始化...";
            System.out.print("\033[33m" + loading + "\033[0m"); // 黄色显示 / Yellow display
            
            for (int i = 0; i < 10; i++) {
                System.out.print("\033[32m.\033[0m");
                Thread.sleep(300);
            }
            
            System.out.println();
            System.out.println();
            
            // 显示系统信息
            // Display system information
            String[] info = {
                "[INFO] Loading core modules...",
                "[INFO] Initializing physics engine...",
                "[INFO] Loading check modules...",
                "[INFO] Setting up world replication...",
                "[INFO] Configuring punishment system...",
                "[INFO] Starting network listeners..."
            };
            
            for (String line : info) {
                System.out.println("\033[36m" + line + "\033[0m"); // 青色显示 / Cyan display
                Thread.sleep(150);
            }
            
            System.out.println();
            System.out.println("\033[32mAQ3 反作弊：Minecraft Paper 服务器全方位防护插件!\033[0m");
            System.out.println("\033[33mhttps://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat\033[0m");
            System.out.println();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 显示关闭动画
     * Display shutdown animation
     */
    public static void showShutdownAnimation() {
        try {
            System.out.println("\033[31m[AQAC] Shutting down anti-cheat system...\033[0m");
            
            String[] shutdownMessages = {
                "[INFO] Stopping network listeners...",
                "[INFO] Saving configuration...",
                "[INFO] Cleaning up resources...",
                "[INFO] Closing logs..."
            };
            
            for (String message : shutdownMessages) {
                System.out.println("\033[36m" + message + "\033[0m");
                Thread.sleep(100);
            }
            
            System.out.println("\033[32m[AQAC] System shutdown complete.\033[0m");
            System.out.println("\033[33mhttps://github.com/XiaoYuan08-Mc\033[0m");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}