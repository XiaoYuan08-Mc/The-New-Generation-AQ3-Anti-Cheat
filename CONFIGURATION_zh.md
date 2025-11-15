# AntiLag 插件配置说明

## 配置文件位置

配置文件位于 `plugins/AntiLag/config.yml`

## 配置项详解

### 物品清理设置 (item)
控制地面物品的清理行为

```yaml
item:
  # 物品在地面上的存活时间（ticks，20 ticks = 1秒）
  # 默认值：300（15秒）
  despawn-time: 300
  
  # 检查间隔（ticks）
  # 默认值：200（10秒）
  check-interval: 200
```

### 实体优化设置 (entity)
控制各种实体的清理和限制

```yaml
entity:
  # 箭头的清理时间（ticks）
  # 默认值：100（5秒）
  arrow-despawn-time: 100
  
  # 其他实体的清理时间（ticks）
  # 默认值：6000（5分钟）
  general-despawn-time: 6000
  
  # 每个区块的最大实体数
  # 默认值：50
  max-per-chunk: 50
```

### 视距设置 (view-distance)
控制服务器视距的动态调整

```yaml
view-distance:
  # 无人在线时的最小视距
  # 默认值：4
  min: 4
  
  # 有人在线时的最大视距
  # 默认值：10
  max: 10
  
  # 是否自动调整视距
  # 默认值：true
  auto-adjust: true
```

### 挂机设置 (afk)
控制挂机玩家的检测和处理

```yaml
afk:
  # 是否启用挂机踢出功能
  # 默认值：true
  enabled: true
  
  # 挂机踢出时间（秒）
  # 默认值：900（15分钟）
  kick-time: 900
```

### 世界设置 (world)
控制世界相关的优化设置

```yaml
world:
  # 是否启用自动保存世界功能
  # 当没有玩家在线时自动保存世界数据
  # 默认值：true
  auto-save: true
```

### 守护模式设置 (guardian-mode)
控制服务器在无玩家在线时的资源节省模式

```yaml
guardian-mode:
  # 是否启用守护模式
  # 默认值：true
  enabled: true
  
  # 进入守护模式的延迟时间（秒）
  # 默认值：300（5分钟）
  delay: 300
```

### 移动限制设置 (movement)
控制特殊移动方式的限制

```yaml
movement:
  # 是否限制鞘翅使用以防止区块过载
  # 默认值：true
  limit-elytra: true
  
  # 是否限制三叉戟使用以防止区块过载
  # 默认值：true
  limit-trident: true
```

### 内存管理设置 (memory)
控制服务器内存使用和垃圾回收

```yaml
memory:
  # 是否启用自动垃圾回收
  # 默认值：true
  auto-gc: true
  
  # 触发垃圾回收的内存使用率阈值（0.0-1.0）
  # 默认值：0.8（80%）
  gc-threshold: 0.8
```

### 漏斗设置 (hopper)
控制漏斗的传输限制

```yaml
hopper:
  # 漏斗传输限制
  # 默认值：1
  transfer-limit: 1
```

## 配置修改后生效方式

配置修改后可以通过以下方式生效：

1. 重启服务器
2. 使用命令 `/antilag reload` 或 `/opt reload` 重新加载配置

## 注意事项

1. 时间单位说明：
   - ticks：Minecraft游戏刻，20 ticks = 1秒
   - seconds：秒

2. 视距设置说明：
   - 最小视距：减少服务器需要处理的区块数量
   - 最大视距：提供更好的游戏体验
   - 自动调整：根据在线玩家数量动态调整

3. 实体限制说明：
   - 限制每个区块的实体数量可以防止实体过多导致的卡顿
   - 箭头等临时实体应该尽快清理

4. 内存管理说明：
   - 设置合适的GC阈值可以防止内存溢出
   - 过低的阈值可能导致频繁GC影响性能