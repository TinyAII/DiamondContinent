# DiamondContinent 钻石大陆

一个 Paper 插件：把服务器变成一个 **超平坦 200 层全钻石矿** 的大陆，含多要塞、新手礼包、出生保护，玩家进服自动传送到钻石大陆。零依赖、装进 plugins/ 即可，开箱即用。

A Paper plugin that turns your server into a **flat continent made of 200 layers of diamond ore**, with multiple strongholds, a starter kit, spawn protection, and auto-teleport to the continent on join. Zero dependencies — drop it in plugins/ and it just works.

---

## 功能 Features

- 🌍 **超平坦 200 层全钻石矿大陆** Flat continent of 200 diamond-ore layers（底部基岩 + 200 层钻石矿，地表直接露矿，往下挖全是钻石）
- 🏰 **多要塞** Multiple strongholds（每 30000 格一个，代码生成类原版地下迷宫，含末地传送门房间）
- 🎁 **新手礼包** Starter kit（每人首次进服自动发放：铁甲套 + 精准采集铁镐 + 岩浆/水桶 + 牛羊猪鸡蛋 + 甘蔗 + 树苗 + 泥土）
- 🛡️ **出生点保护** Spawn protection（出生点 100 格内不能破坏/放置）
- 🚪 **进服自动传送** Auto-teleport（玩家一进服自动传送到钻石大陆；死亡后也重生在钻石大陆）
- 📋 **全面启动日志** Detailed startup logs（创建世界、生成要塞全程可见）

## 安装 Installation

1. 把 `diamondcontinent-2.0.0.jar` 放入服务器的 `plugins/` 文件夹
2. 启动（或 reload）服务器
3. 完事 —— 插件会自动创建钻石大陆世界并生成要塞

Drop `diamondcontinent-2.0.0.jar` into `plugins/`, start (or reload) the server, done. The plugin auto-creates the diamond continent world and its strongholds.

## 命令 Commands

| 命令 Command | 说明 Description |
|---|---|
| `/钻石大陆` 或 `/diamondcontinent` | 查看插件信息 |
| `/钻石大陆 传送` (`/dc tp`) | 传送到钻石大陆 |

> 别名 Alias：`/dc`、`/钻石大陆`

## 配置 Configuration

无需配置即可运行（默认参数已内置）。当前无独立 config.yml，所有核心功能默认开启。

No config needed — defaults are built in. Currently no separate config.yml; all core features are enabled by default.

## 兼容 Compatibility

- 核心：Paper（1.21.8 测试通过，理论上 1.21.x 兼容）
- Java：21
- 零依赖（无前置插件）

Core: Paper (tested on 1.21.8, 1.21.x should work). Java 21. Zero dependencies.

## 常见问题 FAQ

- **出生后在哪里？** 进服自动传送到钻石大陆出生点，脚下就是钻石矿。
- **要塞在哪？** 每 30000 格一个，用代码生成的地下迷宫，含末地传送门房间。

---

© TinyAII · 免费但闭源（Free but closed-source）· 仅发布 jar，不放源码
