# AQ3 反作弊配置指南

本文档提供了关于配置AQ3反作弊插件的详细信息。

## 配置文件

插件在首次启动时会生成两个主要配置文件：

1. `config.yml` - 主配置文件
2. `admins.yml` - 管理员和免疫玩家配置文件

## 主配置文件 (config.yml)

### 检查配置

插件包含多个类别的作弊检测：

```yaml
checks:
  # 移动检查
  movement:
    enabled: true
    
    # 速度检查
    speed:
      enabled: true
      max-violations: 10
      
    # 飞行检查
    fly:
      enabled: true
      max-violations: 10
      
    # 水面行走检查 (Jesus)
    jesus:
      enabled: true
      max-violations: 10
      
    # 计时器检查
    timer:
      enabled: true
      max-violations: 10
      
    # 相位检查 (穿墙)
    phase:
      enabled: true
      max-violations: 10
      
    # 速度异常检查
    velocity:
      enabled: true
      max-violations: 10

  # 战斗检查
  combat:
    enabled: true
    
    # 杀戮光环检查
    killaura:
      enabled: true
      max-violations: 10
      
    # 攻击距离检查
    reach:
      enabled: true
      max-violations: 10
      
    # 自动点击器检查
    autoclicker:
      enabled: true
      max-violations: 10
      
    # 暴击检查
    criticals:
      enabled: true
      max-violations: 10
      
    # 实体交互检查
    entityinteract:
      enabled: true
      max-violations: 10

  # 物品检查
  inventory:
    enabled: true
    
    # 快速使用检查
    fastuse:
      enabled: true
      max-violations: 10
      
    # 背包移动检查
    inventorymove:
      enabled: true
      max-violations: 10
      
  # 世界检查
  world:
    enabled: true
    
    # 脚手架检查
    scaffold:
      enabled: true
      max-violations: 10
      
    # 快速破坏检查
    fastbreak:
      enabled: true
      max-violations: 10
      
    # 范围挖掘检查
    nuker:
      enabled: true
      max-violations: 10

  # 玩家检查
  player:
    enabled: true
    
    # 无摔落伤害检查
    nofall:
      enabled: true
      max-violations: 10
      
    # 生命值恢复检查
    regen:
      enabled: true
      max-violations: 10
      
    # 聊天检查
    chat:
      enabled: true
      max-violations: 10
      
  # 网络检查
  network:
    enabled: true
    
    # 延迟欺骗检查
    pingspoof:
      enabled: true
      max-violations: 10
```

### 处罚配置

配置插件如何处理违规行为：

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
  
  # AdvancedBan集成设置
  use-advancedban: true
  advancedban-temp-ban-duration: 86400000 # 24小时（毫秒）
  advancedban-ban-reason: "[AQAC] 作弊行为检测"
  advancedban-kick-reason: "[AQAC] 作弊行为检测"
```

### 硬件标识配置

配置基于硬件标识的封禁防护：

```yaml
hwid:
  enabled: true
  ban-hwid-on-ban: true
  max-accounts-per-hwid: 0
```

### 灵敏度设置

调整检测灵敏度（1-5，其中5为最高灵敏度）：

```yaml
sensitivity: 3
```

## 管理员配置文件 (admins.yml)

配置管理员和免疫玩家：

```yaml
# 管理员列表
admins:
  - "admin_player_name"
  - "another_admin_name"

# 免疫玩家（不会被检查作弊）
immune-players:
  - "trusted_player_name"
  - "another_trusted_player"
```

## AdvancedBan集成详情

### 概述

AQ3反作弊可以与AdvancedBan插件集成，使用其高级处罚功能。当AdvancedBan可用且在配置中启用时，AQ3将使用AdvancedBan的处罚系统而不是内置系统。

### 配置选项

1. `use-advancedban` - 启用或禁用AdvancedBan集成（默认：true）
2. `advancedban-temp-ban-duration` - 临时封禁的持续时间（毫秒）（默认：86400000，即24小时）
3. `advancedban-ban-reason` - 使用AdvancedBan时的自定义封禁原因
4. `advancedban-kick-reason` - 使用AdvancedBan时的自定义踢出原因

### 行为

- 当AdvancedBan启用且可用时，所有封禁/踢出操作将由AdvancedBan处理
- 如果AdvancedBan不可用或已禁用，插件将回退到内置封禁系统
- 基于HWID的封禁仍由AQ3处理，无论AdvancedBan集成状态如何
- 临时封禁使用`advancedban-temp-ban-duration`中指定的持续时间
- 永久封禁在`ban-duration`设置为-1时使用

### 安装要求

要使用AdvancedBan集成：
1. 在服务器上安装AdvancedBan插件
2. 确保config中的`use-advancedban`设置为true
3. 重启服务器

在AdvancedBan中不需要额外配置即可实现基本集成。