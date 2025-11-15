# AntiLag - Minecraft服务器性能优化插件

AntiLag是一个为Paper服务器设计的插件，旨在通过清理地面物品、优化实体管理和调整服务器设置来减少延迟并提高性能。

GitHub仓库: [https://github.com/XiaoYuan08-Mc/AntiLag](https://github.com/XiaoYuan08-Mc/AntiLag)

## 功能特性

### 1. 地面物品清理
- 自动清理长时间存在的地面物品
- 可配置的清理间隔和物品存活时间
- 减少服务器内存占用和卡顿

### 2. 实体优化
- 自动清理箭头和其他实体（可配置时间）
- 限制每个区块的最大实体数量
- 提高服务器整体性能

### 3. 视距管理
- 根据在线玩家数量自动调整视距
- 无人在线时降低视距以减少服务器负载
- 有玩家在线时恢复正常的视距

### 4. AFK玩家管理
- 自动踢出长时间挂机的玩家（可配置）
- OP权限玩家豁免

### 5. 守护模式
- 当服务器在指定时间内没有玩家在线时进入守护模式
- 进一步降低资源使用

### 6. 内存管理
- 当内存使用率达到阈值时自动触发垃圾回收
- 可通过命令手动触发GC

### 7. 世界自动保存
- 在没有玩家在线时定期保存世界数据
- 减少高峰期的I/O操作

### 8. 移动限制
- 可选限制鞘翅和三叉戟的使用以防止区块过载

## 配置说明

详细配置说明请查看：
- [中文配置说明](CONFIGURATION_zh.md)
- [English Configuration Guide](CONFIGURATION.md)

简要配置项如下：

```yaml
# Item cleanup settings
item:
  # 物品在地面上的存活时间（ticks）
  despawn-time: 300
  
  # 检查间隔（ticks）
  check-interval: 200

# Entity optimization settings
entity:
  # 箭头的清理时间（ticks）
  arrow-despawn-time: 100
  
  # 其他实体的清理时间（ticks）
  general-despawn-time: 6000
  
  # 每个区块的最大实体数
  max-per-chunk: 50

# View distance settings
view-distance:
  # 无人在线时的最小视距
  min: 4
  
  # 有人在线时的最大视距
  max: 10
  
  # 是否自动调整视距
  auto-adjust: true

# AFK settings
afk:
  # 是否启用AFK踢出
  enabled: true
  
  # AFK踢出时间（秒）
  kick-time: 900

# World settings
world:
  # 是否启用自动保存世界
  auto-save: true

# Guardian mode settings
guardian-mode:
  # 是否启用守护模式
  enabled: true
  
  # 进入守护模式的延迟时间（秒）
  delay: 300

# Movement limitation settings
movement:
  # 是否限制鞘翅使用
  limit-elytra: true
  
  # 是否限制三叉戟使用
  limit-trident: true

# Memory management settings
memory:
  # 是否启用自动GC
  auto-gc: true
  
  # 触发GC的内存使用率阈值（0.0-1.0）
  gc-threshold: 0.8

# Hopper settings
hopper:
  # 漏斗传输限制
  transfer-limit: 1
```

## 命令

### 主要命令
- `/antilag reload` - 重新加载配置
- `/antilag stats` - 显示优化统计信息
- `/antilag clear` - 立即清理地面物品

### 优化命令
- `/opt reload` - 重新加载配置
- `/opt gc` - 手动触发垃圾回收
- `/opt clear` - 立即清理地面物品

## 权限

- `antilag.admin` - 允许使用所有命令（默认为OP）

## 安装

1. 将生成的 `antilag-1.0-SNAPSHOT.jar` 文件放入服务器的 `plugins` 文件夹
2. 重启服务器以生成配置文件
3. 根据需要修改 `config.yml` 配置文件
4. 再次重启服务器或使用 `/antilag reload` 命令加载配置

## 性能优化建议

1. **定期清理**：插件会自动清理地面物品和多余实体，但也可以手动执行清理
2. **内存管理**：配置合适的GC阈值以防止内存溢出
3. **视距调整**：合理设置最大和最小视距以平衡性能和游戏体验
4. **AFK管理**：启用AFK踢出以释放空闲玩家占用的资源
5. **守护模式**：在无人在线时启用守护模式以进一步降低资源使用

## 故障排除

如果遇到问题，请检查以下几点：
1. 确保使用的是Paper服务器
2. 检查配置文件是否有语法错误
3. 查看服务器日志以获取错误信息
4. 确认插件版本与服务器版本兼容

## 开源许可

本项目采用MIT许可证开源，详情请查看 [LICENSE](LICENSE) 文件。

## 支持

如有问题或建议，请联系插件开发者或提交GitHub Issue。