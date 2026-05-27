package cn.aq3.anticheat.bukkit;

import cn.aq3.anticheat.AQ3API;
import cn.aq3.anticheat.checks.Check;
import cn.aq3.anticheat.checks.world.FastBreakCheck;
import cn.aq3.anticheat.checks.world.XrayCheck;
import cn.aq3.anticheat.player.PlayerData;
import cn.aq3.anticheat.violations.ViolationManager;
import cn.aq3.anticheat.world.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.List;
import java.util.UUID;

/**
 * 玩家事件监听器
 * Player event listener
 */
public class PlayerListener implements Listener {
    private final AQ3Bukkit plugin;
    
    public PlayerListener(AQ3Bukkit plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // 检查玩家是否因硬件标识被封禁
        // Check if player is banned due to hardware identifier
        String playerName = player.getName();
        String playerHWID = AQ3API.getInstance().getHWIDManager().generateHWID();
        
        if (AQ3API.getInstance().getHWIDManager().isHWIDBanned(playerHWID)) {
            // 硬件标识被封禁，拒绝玩家加入
            // Hardware identifier banned, deny player join
            player.kickPlayer("[AQAC] 此硬件标识已被封禁");
            return;
        }
        
        // 创建玩家数据
        PlayerData playerData = new PlayerData(playerUUID, playerName);
        playerData.setHealth(player.getHealth());
        playerData.setFoodLevel(player.getFoodLevel());
        AQ3API.getInstance().getPlayerDataManager().addPlayerData(playerData);
        
        // 为玩家创建世界副本
        WorldManager worldManager = AQ3API.getInstance().getWorldManager();
        worldManager.createWorldForPlayer(playerUUID);
        
        // 注册玩家的硬件标识
        // Register player's hardware identifier
        AQ3API.getInstance().getHWIDManager().registerPlayerHWID(playerName, playerHWID);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // 移除玩家数据
        AQ3API.getInstance().getPlayerDataManager().removePlayerData(playerUUID);
        
        // 移除玩家世界副本
        AQ3API.getInstance().getWorldManager().removeWorldForPlayer(playerUUID);
        
        // 重置Xray检查数据
        XrayCheck xrayCheck = (XrayCheck) AQ3API.getInstance().getCheckManager().getCheck(XrayCheck.class);
        if (xrayCheck != null) {
            xrayCheck.resetPlayerData(playerUUID.toString());
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            // 更新玩家位置数据
            playerData.updatePosition(
                event.getTo().getX(),
                event.getTo().getY(),
                event.getTo().getZ(),
                event.getTo().getBlock().getType().isSolid()
            );
            
            // 更新玩家视角数据
            playerData.updateLook(
                event.getTo().getYaw(),
                event.getTo().getPitch()
            );
            
            // 更新玩家蹲下状态
            playerData.updateSneaking(player.isSneaking());
            
            // 检查是否发生了实际的位置移动
            // Check if actual position movement occurred
            boolean positionChanged = !(event.getFrom().getBlockX() == event.getTo().getBlockX() && 
                                       event.getFrom().getBlockY() == event.getTo().getBlockY() && 
                                       event.getFrom().getBlockZ() == event.getTo().getBlockZ());
            
            // 只有在位置发生变化时才执行移动检查
            // Only perform movement checks when position changes
            if (positionChanged) {
                performChecks(playerUUID, playerData);
            }
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            // 更新玩家蹲下状态
            playerData.updateSneaking(event.isSneaking());
            
            // 执行检查
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            playerData.setSprinting(event.isSprinting());
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            playerData.setFlying(event.isFlying());
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            // 如果是合法的游戏模式切换，重置某些检查
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // 检查是否是玩家攻击实体
        // Check if player is attacking entity
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            UUID playerUUID = player.getUniqueId();
            
            PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
            
            if (playerData != null) {
                // 记录攻击时间
                playerData.setLastAttackTime(System.currentTimeMillis());
                
                // 执行检查
                performChecks(playerUUID, playerData);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            playerData.setLastInteractTime(System.currentTimeMillis());
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            playerData.setLastUseTime(System.currentTimeMillis());
            performChecks(playerUUID, playerData);
        }
    }
    
    // 待添加：EntityVelocityEvent 监听器（需要正确的API版本支持）
    // TODO: Add EntityVelocityEvent listener (requires correct API version support)
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            playerData.setLastEatTime(System.currentTimeMillis());
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerUUID = player.getUniqueId();
            
            PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
            
            if (playerData != null) {
                playerData.setFoodLevel(event.getFoodLevel());
                performChecks(playerUUID, playerData);
            }
        }
    }
    
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerUUID = player.getUniqueId();
            
            PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
            
            if (playerData != null) {
                playerData.setLastHealTime(System.currentTimeMillis());
                playerData.setHealth(player.getHealth() + event.getAmount());
                performChecks(playerUUID, playerData);
            }
        }
    }
    
    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            double newExp = player.getExp() + event.getAmount();
            playerData.setExperience(newExp);
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            // 记录开始破坏方块的时间
            playerData.setLastBreakStartTime(System.currentTimeMillis());
            
            // 通知FastBreak检查开始破坏方块
            FastBreakCheck fastBreakCheck = (FastBreakCheck) AQ3API.getInstance().getCheckManager().getCheck(FastBreakCheck.class);
            if (fastBreakCheck != null) {
                fastBreakCheck.startBreakingBlock(playerUUID.toString(), event.getBlock().getType().name());
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            // 更新破坏方块计数
            playerData.setBlockBreakCount(playerData.getBlockBreakCount() + 1);
            playerData.setLastBreakCheckTime(System.currentTimeMillis());
            
            // 通知FastBreak检查完成破坏方块
            FastBreakCheck fastBreakCheck = (FastBreakCheck) AQ3API.getInstance().getCheckManager().getCheck(FastBreakCheck.class);
            if (fastBreakCheck != null) {
                fastBreakCheck.completeBreakingBlock(playerUUID.toString());
            }
            
            // 通知Xray检查记录破坏的方块
            XrayCheck xrayCheck = (XrayCheck) AQ3API.getInstance().getCheckManager().getCheck(XrayCheck.class);
            if (xrayCheck != null) {
                xrayCheck.recordBlockMined(playerUUID.toString(), event.getBlock().getType().name());
            }
            
            // 执行检查
            performChecks(playerUUID, playerData);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        PlayerData playerData = AQ3API.getInstance().getPlayerDataManager().getPlayerData(playerUUID);
        
        if (playerData != null) {
            // 更新方块放置计数
            playerData.setBlockPlaceCount(playerData.getBlockPlaceCount() + 1);
            playerData.setLastPlaceCheckTime(System.currentTimeMillis());
            playerData.setLastBlockPlaceTime(System.currentTimeMillis());
            
            // 执行检查
            performChecks(playerUUID, playerData);
        }
    }
    
    /**
     * 执行所有检查
     * Perform all checks
     */
    private void performChecks(UUID playerUUID, PlayerData playerData) {
        // 执行所有启用的检查
        // Perform all enabled checks
        List<Check> failedChecks = AQ3API.getInstance().getCheckManager().performChecks(playerData);
        
        // 处理失败的检查
        // Handle failed checks
        if (!failedChecks.isEmpty()) {
            ViolationManager violationManager = AQ3API.getInstance().getViolationManager();
            for (Check check : failedChecks) {
                violationManager.addViolation(playerUUID, check);
                
                // 处理违规行为
                // Handle violation
                AQ3API.getInstance().getPunishmentManager().handleViolation(
                    playerUUID, playerData.getName(), check);
            }
        }
    }
}
