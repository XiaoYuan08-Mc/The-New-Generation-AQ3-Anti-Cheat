<<<<<<< HEAD
# AntiLag Plugin Configuration

## Configuration File Location

The configuration file is located at `plugins/AntiLag/config.yml`

## Configuration Details

### Item Cleanup Settings (item)
Controls the cleanup behavior of ground items

```yaml
item:
  # Time in ticks (20 ticks = 1 second) after which items will be removed
  # Default: 300 (15 seconds)
  despawn-time: 300
  
  # Interval in ticks to check for items to remove
  # Default: 200 (10 seconds)
  check-interval: 200
```

### Entity Optimization Settings (entity)
Controls cleanup and limits for various entities

```yaml
entity:
  # Time in ticks after which arrows will be removed
  # Default: 100 (5 seconds)
  arrow-despawn-time: 100
  
  # General despawn time for other entities (ticks)
  # Default: 6000 (5 minutes)
  general-despawn-time: 6000
  
  # Maximum entities allowed per chunk
  # Default: 50
  max-per-chunk: 50
```

### View Distance Settings (view-distance)
Controls dynamic adjustment of server view distance

```yaml
view-distance:
  # Minimum view distance when no players are online
  # Default: 4
  min: 4
  
  # Maximum view distance when players are online
  # Default: 10
  max: 10
  
  # Automatically adjust view distance
  # Default: true
  auto-adjust: true
```

### AFK Settings (afk)
Controls detection and handling of AFK players

```yaml
afk:
  # Enable AFK kick feature
  # Default: true
  enabled: true
  
  # Time in seconds before kicking AFK players
  # Default: 900 (15 minutes)
  kick-time: 900
```

### World Settings (world)
Controls world-related optimization settings

```yaml
world:
  # Enable automatic world saving
  # Automatically save world data when no players are online
  # Default: true
  auto-save: true
```

### Guardian Mode Settings (guardian-mode)
Controls resource saving mode when no players are online

```yaml
guardian-mode:
  # Enable guardian mode
  # Default: true
  enabled: true
  
  # Delay in seconds before entering guardian mode
  # Default: 300 (5 minutes)
  delay: 300
```

### Movement Limitation Settings (movement)
Controls restrictions on special movement methods

```yaml
movement:
  # Limit elytra usage to prevent chunk overloading
  # Default: true
  limit-elytra: true
  
  # Limit trident usage to prevent chunk overloading
  # Default: true
  limit-trident: true
```

### Memory Management Settings (memory)
Controls server memory usage and garbage collection

```yaml
memory:
  # Enable automatic garbage collection
  # Default: true
  auto-gc: true
  
  # Memory threshold (0.0-1.0) to trigger GC
  # Default: 0.8 (80%)
  gc-threshold: 0.8
```

### Hopper Settings (hopper)
Controls hopper transfer limits

```yaml
hopper:
  # Hopper transfer limit
  # Default: 1
  transfer-limit: 1
```

## Applying Configuration Changes

Configuration changes can take effect in the following ways:

1. Restart the server
2. Use the command `/antilag reload` or `/opt reload` to reload the configuration

## Notes

1. Time unit explanations:
   - ticks: Minecraft game ticks, 20 ticks = 1 second
   - seconds: seconds

2. View distance settings:
   - Minimum view distance: Reduces the number of chunks the server needs to process
   - Maximum view distance: Provides a better gaming experience
   - Auto-adjust: Dynamically adjusts based on the number of online players

3. Entity limitations:
   - Limiting the number of entities per chunk can prevent lag caused by too many entities
   - Temporary entities like arrows should be cleaned up quickly

4. Memory management:
   - Setting an appropriate GC threshold can prevent out-of-memory errors
   - Too low a threshold may cause frequent GC affecting performance
=======
# AQ3 Anti-Cheat Configuration Guide

This document provides detailed information about configuring the AQ3 Anti-Cheat plugin.

## Configuration Files

The plugin generates two main configuration files on first startup:

1. `config.yml` - Main configuration file
2. `admins.yml` - Administrator and immune player configuration

## Main Configuration (config.yml)

### Checks Configuration

The plugin includes multiple categories of cheat detection:

```yaml
checks:
  # Movement checks
  movement:
    enabled: true
    
    # Speed check
    speed:
      enabled: true
      max-violations: 10
      
    # Fly check
    fly:
      enabled: true
      max-violations: 10
      
    # Jesus check (water walking)
    jesus:
      enabled: true
      max-violations: 10
      
    # Timer check
    timer:
      enabled: true
      max-violations: 10
      
    # Phase check (wall clipping)
    phase:
      enabled: true
      max-violations: 10
      
    # Velocity check
    velocity:
      enabled: true
      max-violations: 10

  # Combat checks
  combat:
    enabled: true
    
    # Kill Aura check
    killaura:
      enabled: true
      max-violations: 10
      
    # Reach check
    reach:
      enabled: true
      max-violations: 10
      
    # Auto Clicker check
    autoclicker:
      enabled: true
      max-violations: 10
      
    # Criticals check
    criticals:
      enabled: true
      max-violations: 10
      
    # Entity Interact check
    entityinteract:
      enabled: true
      max-violations: 10

  # Inventory checks
  inventory:
    enabled: true
    
    # Fast use check
    fastuse:
      enabled: true
      max-violations: 10
      
    # Inventory Move check
    inventorymove:
      enabled: true
      max-violations: 10
      
  # World checks
  world:
    enabled: true
    
    # Scaffold check
    scaffold:
      enabled: true
      max-violations: 10
      
    # Fast break check
    fastbreak:
      enabled: true
      max-violations: 10
      
    # Nuker check
    nuker:
      enabled: true
      max-violations: 10

  # Player checks
  player:
    enabled: true
    
    # NoFall check
    nofall:
      enabled: true
      max-violations: 10
      
    # Regen check
    regen:
      enabled: true
      max-violations: 10
      
    # Chat check
    chat:
      enabled: true
      max-violations: 10
      
  # Network checks
  network:
    enabled: true
    
    # Ping Spoof check
    pingspoof:
      enabled: true
      max-violations: 10
```

### Punishment Configuration

Configure how the plugin handles violations:

```yaml
punishments:
  enabled: true
  kick-threshold: 100
  ban-threshold: 500
  ban-on-detection: true
  ban-duration: -1
  ban-reason: "[AQAC] 作弊行为检测"
  kick-message: "[AQAC] 作弊行为检测"
  broadcast-ban: true
  
  # AdvancedBan integration settings
  use-advancedban: true
  advancedban-temp-ban-duration: 86400000 # 24 hours in milliseconds
  advancedban-ban-reason: "[AQAC] 作弊行为检测"
  advancedban-kick-reason: "[AQAC] 作弊行为检测"
```

### Hardware Identifier Configuration

Configure HWID-based ban prevention:

```yaml
hwid:
  enabled: true
  ban-hwid-on-ban: true
  max-accounts-per-hwid: 0
```

### Sensitivity Settings

Adjust detection sensitivity (1-5, where 5 is highest sensitivity):

```yaml
sensitivity: 3
```

## Administrator Configuration (admins.yml)

Configure administrators and immune players:

```yaml
# Administrator list
admins:
  - "admin_player_name"
  - "another_admin_name"

# Immune players (will not be checked for cheats)
immune-players:
  - "trusted_player_name"
  - "another_trusted_player"
```

## AdvancedBan Integration Details

### Overview

AQ3 Anti-Cheat can integrate with the AdvancedBan plugin to use its advanced punishment features. When AdvancedBan is available and enabled in the configuration, AQ3 will use AdvancedBan's punishment system instead of the built-in one.

### Configuration Options

1. `use-advancedban` - Enable or disable AdvancedBan integration (default: true)
2. `advancedban-temp-ban-duration` - Duration for temporary bans in milliseconds (default: 86400000, which is 24 hours)
3. `advancedban-ban-reason` - Customizable ban reason when using AdvancedBan
4. `advancedban-kick-reason` - Customizable kick reason when using AdvancedBan

### Behavior

- When AdvancedBan is enabled and available, all ban/kick operations will be handled by AdvancedBan
- If AdvancedBan is not available or disabled, the plugin falls back to the built-in ban system
- HWID-based bans are still handled by AQ3 regardless of AdvancedBan integration status
- Temporary bans use the duration specified in `advancedban-temp-ban-duration`
- Permanent bans are used when `ban-duration` is set to -1

### Installation Requirements

To use AdvancedBan integration:
1. Install the AdvancedBan plugin on your server
2. Ensure `use-advancedban` is set to true in the config
3. Restart the server

No additional configuration is required in AdvancedBan itself for basic integration.
>>>>>>> 281dd9bd526ec4673437856bc9449da4813b7917
