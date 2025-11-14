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