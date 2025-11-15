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