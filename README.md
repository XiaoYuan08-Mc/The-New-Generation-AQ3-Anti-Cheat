<div align="center">

# 🛡️ AQ3 反作弊

## 新一代反作弊系统

[![Build](https://img.shields.io/badge/Build-passing-red?style=for-the-badge)](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)
[![Version](https://img.shields.io/badge/VERSION-1.3.0-black?style=for-the-badge)](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-red?style=for-the-badge)](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)
[![License](https://img.shields.io/badge/LICENSE-GPL--3.0-red?style=for-the-badge)](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat/blob/main/LICENSE)

### ⚡ 精确检测 · GrimAc风格 · 运动预测 · 概率系统 ⚡

</div>

---

## 📋 项目简介

**AQ3 反作弊** 是一个面向 Minecraft Paper 服务器的先进反作弊插件。它采用了精确的运动模拟引擎，能够 1:1 复制玩家的所有可能动作，包括基础移动、游泳、击退、蜘蛛网效果、气泡柱以及乘坐船、猪、炽足兽等实体。

**支持版本**: Minecraft 1.21.x (Paper API 1.21.3-R0.1)

本项目已完全开源，代码托管在 [GitHub](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)。

---

## ✨ 核心特性

### 🔍 检测功能

| 类别 | 检测项 | 描述 |
|------|--------|------|
| **移动类** | 飞行检测 | 检测异常的飞行和悬浮行为 |
| | 速度检测 | 检测异常的移动速度 |
| | 水上行走 | 检测在水上行走的作弊行为 |
| | 跳跃检测 | 检测不自然的跳跃行为 |
| | 鞘翅检测 | 检测异常的鞘翅飞行行为 |
| | 传送检测 | 检测可疑的传送行为 |
| | 相位检测 | 检测穿墙作弊 |
| **战斗类** | 杀戮光环 | 检测自动攻击多个目标的作弊 |
| | 自动点击 | 检测异常的点击模式和频率 |
| | 远程攻击 | 检测异常的攻击距离 |
| | 致命一击 | 检测非法的暴击作弊 |
| | 防击退 | 检测防击退（AntiKB）作弊 |
| **世界类** | 快速破坏 | 检测异常的方块破坏速度 |
| | 快速挖掘 | 检测不自然的挖掘行为 |
| | 脚手架 | 检测自动搭路作弊 |
| | 开矿透视 | 检测可能使用X射线的挖掘模式 |
| | 核爆破坏 | 检测同时破坏多个方块的作弊 |
| | 方块放置 | 检测异常的方块放置行为 |
| **玩家类** | 防摔落 | 检测避免摔落伤害的作弊 |
| | 速度加速 | 检测加速移动作弊 |
| | 生命恢复 | 检测异常的生命恢复速度 |
| | 物品使用 | 检测异常的物品使用频率 |
| | 经验值异常 | 检测异常的经验值获取 |
| **网络类** | 延迟伪装 | 检测网络延迟作弊 |
| **机器学习** | 模式分析 | 分析复杂的作弊模式 |

### 🛠️ 系统特性

- 🧪 **精确的运动模拟引擎** - 1:1复制玩家所有可能动作
- 🔄 **跨版本兼容性** - 支持多种Minecraft版本
- ⚡ **高性能架构** - 异步和多线程设计，世界复制系统
- 📊 **全面的检测类别** - 超过35种检测模块
- 🪪 **硬件标识检测** - HWID封禁系统，防止小号
- 🔗 **AdvancedBan集成** - 与AdvancedBan无缝对接
- 🎯 **可配置的检测灵敏度** - 支持多种检测灵敏度设置
- 💾 **数据持久化** - 使用YAML格式保存配置和数据
- 📈 **GrimAc风格检测** - 精确运动预测、概率检测系统
- 🎯 **精确Reach检测** - 精准攻击距离计算
- 📊 **概率检测系统** - 使用统计学分析作弊概率
- 🔮 **运动预测引擎** - 预测玩家下一步位置并验证

---

## 🚀 快速开始

### 安装

```bash
# 克隆仓库
git clone https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat.git

# 编译项目
cd The-New-Generation-AQ3-Anti-Cheat
mvn clean package
```

### 配置

编辑 `plugins/AQ3AntiCheat/config.yml` 配置文件，自定义您的反作弊设置。

---

## 📋 系统架构

```
AQ3 AntiCheat
├── aq3-anticheat-common (核心模块)
│   ├── checks (检测模块)
│   │   ├── combat (战斗类)
│   │   ├── movement (移动类)
│   │   ├── player (玩家类)
│   │   └── world (世界类)
│   ├── config (配置管理)
│   ├── hwid (硬件标识)
│   ├── physics (物理引擎)
│   ├── player (玩家数据)
│   ├── violations (违规管理)
│   └── world (世界管理)
└── aq3-anticheat-bukkit (平台适配)
    └── listener (事件监听器)
```

---

## 📄 开源许可证

本项目采用 GNU General Public License v3.0 许可证。

您必须：
- 保留原始版权声明和许可证声明
- 在分发修改版本时使用相同的许可证
- 提供源代码

更多信息请查看 [LICENSE](LICENSE) 文件。

---

## 🔗 项目地址

- 🚀 [GitHub 仓库](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)
- 👀 [完整设计预览](https://htmlpreview.github.io/?https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat/blob/main/title-design.html)

---

## 📝 更新日志

### v1.3.0 (最新)
- 🎮 添加 GrimAc风格的精确检测系统
- 📈 新增 精确Reach检测 (PrecisionReachCheck)
- 📊 新增 概率检测系统 (ProbabilityCheck)
- 🔮 新增 运动预测引擎 (PredictionEngine)
- 📋 新增 预测运动检测 (PredictionMovementCheck)
- 🧠 改进 作弊检测逻辑，降低误判率
- 🔧 优化 CheckManager，新增3种检测

### v1.2.0
- 🎮 更新 支持 Minecraft 1.21.x
- 📦 升级 Paper API 到 1.21.3-R0.1-SNAPSHOT
- 🔧 优化 插件配置，api-version 设定为 1.21
- 📝 更新 README.md，添加 1.21.x 支持说明

### v1.1.0
- ✨ 新增 X射线（Xray）检测模块
- 🛡️ 改进 防击退（AntiKB）检测系统
- 📊 增强 PlayerData，添加移动历史记录
- 🔧 优化 配置系统，支持更多检测选项
- 📝 更新 README.md，添加完整文档

### v1.0.0
- 🎉 初始版本发布
- 🌟 完整的反作弊系统架构
- 🛡️ 28+ 种检测模块
- 🧪 精确的物理引擎
- 📦 Maven多模块结构

---

<div align="center">

© 2024 AQ3 AntiCheat - 新一代反作弊系统

</div>
