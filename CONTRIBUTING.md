# 贡献指南

感谢您对AQ3反作弊系统的关注！我们欢迎任何形式的贡献。

## 如何贡献

### 报告Bug
如果您发现了Bug，请在GitHub上创建一个Issue，包含以下信息：
- 问题的详细描述
- 重现步骤
- 期望的行为
- 实际的行为
- 环境信息（服务器版本、插件版本等）

### 提交功能请求
如果您有新功能的建议，请创建一个Issue，描述：
- 功能的详细说明
- 解决的问题或满足的需求
- 可能的实现方式

### 提交代码
1. Fork项目
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个Pull Request

## 开发环境设置

### 先决条件
- Java 17或更高版本
- Maven 3.8.0或更高版本
- Paper服务器环境（用于测试）

### 构建项目
```bash
mvn clean install
```

### 代码规范
- 遵循项目现有的代码风格
- 添加适当的注释，特别是中文注释
- 编写单元测试（如果适用）
- 确保所有测试通过

## 代码结构
- `common` - 核心功能和共享代码
- `bukkit` - Bukkit/Spigot/Paper平台实现

## 测试
在提交Pull Request之前，请确保：
- 代码能够成功编译
- 所有现有测试通过
- 添加了适当的测试用例（如果适用）

## 许可证
通过贡献代码，您同意您的贡献将遵循项目的GNU GPL v3许可证。

## 联系方式
如有任何问题，请通过以下方式联系我们：
- GitHub Issues: https://github.com/XiaoYuan08-Mc/issues