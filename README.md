<div align="center" style="background: #0a0a0a; padding: 60px 20px; border-radius: 20px; margin: 20px 0; position: relative; overflow: hidden;">
  <div style="position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #dc143c, #ff4500, #dc143c);"></div>
  <div style="position: absolute; bottom: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #dc143c, #ff4500, #dc143c);"></div>
  
  <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative; z-index: 1;">
    <div style="display: flex; align-items: center; justify-content: center; gap: 30px; margin-bottom: 30px;">
      <div style="width: 140px; height: 140px; background: linear-gradient(135deg, #1a1a1a 0%, #0a0a0a 100%); border: 4px solid #dc143c; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 0 60px rgba(220, 20, 60, 0.6), inset 0 0 30px rgba(220, 20, 60, 0.2); position: relative;">
        <div style="position: absolute; width: 120px; height: 120px; border: 2px solid rgba(220, 20, 60, 0.3); border-radius: 50%; animation: pulse-border 2s ease-in-out infinite;"></div>
        <svg width="70" height="70" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2L3 7V12C3 16.5 6.84 20.74 12 22C17.16 20.74 21 16.5 21 12V7L12 2Z" fill="#dc143c" stroke="#ff4500" stroke-width="1.5"/>
          <path d="M9 12L11 14L15 10" stroke="#ffffff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      
      <div style="text-align: left;">
        <h1 style="font-size: 72px; font-weight: 900; letter-spacing: -4px; margin: 0; line-height: 1; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
          <span style="color: #ffffff; text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);">AQ</span><span style="color: #dc143c; text-shadow: 0 0 30px rgba(220, 20, 60, 0.8);">3</span>
        </h1>
        <div style="font-size: 22px; color: #dc143c; margin-top: 15px; font-weight: 700; letter-spacing: 8px; text-transform: uppercase;">
          新一代反作弊系统
        </div>
      </div>
    </div>

    <div style="font-size: 28px; font-weight: 800; margin: 40px 0 30px 0; color: #ffffff; letter-spacing: 3px;">
      <span style="color: #dc143c;">⚡</span> 精确检测 · 高性能 · 零误判 <span style="color: #dc143c;">⚡</span>
    </div>

    <div style="display: flex; flex-wrap: wrap; gap: 15px; justify-content: center;">
      <span style="padding: 12px 24px; border-radius: 4px; font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px; background: #1a1a1a; color: #dc143c; border: 2px solid #dc143c;">BUILD</span>
      <span style="padding: 12px 24px; border-radius: 4px; font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px; background: #dc143c; color: #ffffff; box-shadow: 0 0 20px rgba(220, 20, 60, 0.5);">PASSING</span>
      <span style="padding: 12px 24px; border-radius: 4px; font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px; background: #1a1a1a; color: #ffffff; border: 2px solid #333;">V1.0.0-BETA</span>
      <span style="padding: 12px 24px; border-radius: 4px; font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px; background: #1a1a1a; color: #dc143c; border: 2px solid #dc143c;">GPL-3.0</span>
    </div>
  </div>
</div>

<style>
@keyframes pulse-border {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.15);
    opacity: 0;
  }
}
</style>

---

## 📋 项目简介

**AQ3 反作弊** 是一个面向 Minecraft Paper 服务器的先进反作弊插件。它采用了精确的运动模拟引擎，能够 1:1 复制玩家的所有可能动作，包括基础移动、游泳、击退、蜘蛛网效果、气泡柱以及乘坐船、猪、炽足兽等实体。

本项目已完全开源，代码托管在 [GitHub](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)。

---

## ✨ 核心特性

| 特性 | 描述 |
|------|------|
| 🧪 **精确运动模拟** | 实现完整的物理引擎，模拟所有玩家行为，覆盖从基本行走到复杂交互的所有动作 |
| 🔄 **跨版本兼容** | 支持多种客户端-服务器版本组合，自动处理版本间的技术差异 |
| ⚡ **高性能架构** | 异步和多线程设计，世界复制系统支持并发处理，延迟补偿机制 |
| 📊 **全面检测类别** | 移动类、战斗类、玩家类、物品类、世界类等28+个检测模块 |
| 🪪 **硬件标识检测** | 自动生成并跟踪玩家的硬件标识，防止被封禁玩家通过创建小号绕过 |
| 🔗 **AdvancedBan集成** | 与 AdvancedBan 插件无缝集成，支持永久封禁和临时封禁 |

---

## 🚀 快速开始

```bash
# 克隆仓库
git clone https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat.git

# 编译项目
cd The-New-Generation-AQ3-Anti-Cheat
mvn clean package
```

---

## 📋 系统架构

- **aq3-anticheat-common** - 核心反作弊逻辑
- **aq3-anticheat-bukkit** - Bukkit平台适配

---

## 📄 开源许可证

本项目采用 GNU General Public License v3.0 许可证。

但您必须：
- 保留原始版权声明和许可证声明
- 在分发修改版本时使用相同的许可证
- 提供源代码

更多信息请查看 [LICENSE](LICENSE) 文件。

---

## 🔗 项目地址

- 🚀 [GitHub 仓库](https://github.com/XiaoYuan08-Mc/The-New-Generation-AQ3-Anti-Cheat)

---

<div align="center" style="margin-top: 50px; padding-top: 30px; border-top: 1px solid #333; color: #a0a0b0;">
  <p>
    © 2024 AQ3 AntiCheat - 新一代反作弊系统
  </p>
</div>
