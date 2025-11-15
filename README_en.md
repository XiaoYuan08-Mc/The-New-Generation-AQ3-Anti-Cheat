# AQ3 Anti-Cheat System

## Project Overview

AQ3 Anti-Cheat is an advanced anti-cheat plugin designed for Minecraft Paper servers. It features a precise motion simulation engine that can replicate all possible player actions with 1:1 accuracy, including basic movement, swimming, knockback, cobweb effects, bubble columns, and riding entities like boats, pigs, and striders.

This project is fully open-source and hosted on [GitHub](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat).

## Core Features

### Precise Motion Simulation
- Complete physics engine implementation simulating all player behaviors
- Covers all actions from basic walking to complex interactions
- Accurately handles edge cases to ensure precision

### Cross-Version Compatibility
- Supports multiple client-server version combinations
- Automatically handles technical differences between versions
- Properly manages hitbox differences across versions

### High-Performance Architecture
- Asynchronous and multi-threaded design
- World replication system supporting concurrent processing
- Lag compensation mechanism

### Comprehensive Detection Categories
- Movement cheats (speed, fly, jump, phase, velocity, etc.)
- Combat cheats (kill aura, reach, auto-clicker, criticals, etc.)
- Player cheats (no fall damage, timer, regen, chat, etc.)
- Inventory cheats (fast use, inventory move, etc.)
- World cheats (scaffold, fast break, etc.)
- Machine learning-based complex cheat patterns

### Hardware Identification (HWID)
- Automatically generates and tracks player hardware identifiers
- Prevents banned players from bypassing bans by creating alt accounts
- Supports associated account detection and bulk banning

### AdvancedBan Integration
- Seamless integration with the AdvancedBan plugin
- Support for both permanent and temporary bans
- Player kicking functionality
- Configurable ban and kick reasons
- Fallback mechanism compatible with built-in ban system

## System Architecture

### Physics Engine
Simulates player physics in-game, including:
- Gravity calculations
- Jump mechanics
- Movement speed
- Environmental effects (water, cobwebs, honey blocks, soul speed blocks, etc.)

### World Management System
Maintains independent world copies for each player:
- Real-time block change synchronization
- Thread-safe access
- Memory-optimized storage

### Check Management System
Manages all anti-cheat checks:
- Movement checks (speed, fly, jesus, phase, velocity, etc.)
- Combat checks (kill aura, reach, auto-clicker, criticals, etc.)
- Inventory checks (fast use, inventory move, etc.)
- World checks (scaffold, fast break, etc.)
- Player checks (no fall, regen, chat, etc.)
- Machine learning pattern analysis checks

### Violation Management
Tracks and manages player violations:
- Records each violation
- Counts violation occurrences
- Triggers appropriate punishments

### Configuration Management
Manages plugin configuration:
- Supports loading configuration from YAML files
- Allows dynamic adjustment of check parameters
- Supports enabling/disabling specific checks

### Punishment Management
Handles violation punishments:
- Executes different punishments based on violation counts
- Supports kick and ban punishment methods
- Supports hardware identifier banning to prevent alt account bypass

### Logging System
Records cheating behavior:
- Detailed cheat logs
- System event logs
- Facilitates auditing and analysis

### Hardware Identification Management
Prevents ban evasion through alt accounts:
- Automatically generates and tracks player hardware identifiers
- Supports banning specific hardware identifiers
- Detects and manages associated accounts

## Installation Instructions

1. Place the generated jar file in the server's plugins directory
2. Restart the server
3. The plugin will automatically generate configuration files
4. Adjust configuration files as needed

## Commands

- `/aq3` - Main command
- `/aq3 reload` - Reload configuration
- `/aq3 version` - Show version information
- `/aq3 hwid` - Hardware identifier management commands

## Permissions

- `aq3.admin` - Administrator permission

## Configuration

Configuration files will be generated on first startup, and you can adjust them according to your server needs.

### Basic Configuration

The system supports basic configuration files where you can set:
- Check enable/disable
- Sensitivity settings
- Punishment thresholds

### Administrator Configuration

The system supports administrator configuration files where you can set:
- Administrator list
- Immune player list
- Punishment settings
- Hardware identifier settings

### AdvancedBan Integration Configuration

The system supports integration with the AdvancedBan plugin where you can set:
- Enable/disable AdvancedBan integration
- Temporary ban duration
- Custom ban and kick reasons

## Hardware Identification Feature

The AQ3 anti-cheat system includes advanced hardware identification (HWID) functionality to prevent banned players from bypassing bans by creating alt accounts.

### Feature Highlights

1. **Automatic HWID Generation** - The system automatically generates unique hardware identifiers for each player
2. **Associated Account Detection** - Can detect multiple accounts using the same hardware identifier
3. **Ban Linking** - When banning a player, you can choose to simultaneously ban their hardware identifier
4. **Command-Line Management** - Provides command-line tools for managing hardware identifiers

### Commands

- `/aq3 hwid list` - List all banned hardware identifiers
- `/aq3 hwid ban <hwid>` - Ban the specified hardware identifier
- `/aq3 hwid unban <hwid>` - Unban the specified hardware identifier
- `/aq3 hwid check <player>` - Check a player's hardware identifier
- `/aq3 hwid info <hwid>` - Show detailed information about a hardware identifier

## Development

This project uses the Maven build system and includes the following modules:

1. `aq3-anticheat-common` - Core anti-cheat logic
2. `aq3-anticheat-bukkit` - Bukkit platform adaptation

## License

This project is licensed under the GNU General Public License v3.0.

However, you must:
- Retain original copyright and license notices
- Use the same license when distributing modified versions
- Provide source code

For more information, see the [LICENSE](LICENSE) file.

The project code is hosted on [GitHub](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat).