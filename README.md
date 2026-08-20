# DiamondContinent 钻石大陆

> 把服务器变成一片「超平坦 200 层全钻石矿」的大陆，挖矿刷钻爬到爽。零依赖，开箱即用。

装了它，进服脚下就是钻石矿、往下挖 200 格全是钻，地图里散布着 48 个带末地传送门的地下要塞，新手人人有精准采集镐开路。适合生存/挖矿服撑排面。

> **MIT 开源** · 零依赖 · 可自由使用/修改/商用（保留 TinyAII 署名）

---

## 功能

| 功能 | 说明 |
| --- | --- |
| 🌍 超平坦 200 层钻石矿大陆 | 底部基岩封底，地表直接露矿，往下挖全是钻石 |
| 🏰 多要塞 | 每 30000 格一个，代码生成类原版地下迷宫，含末地传送门房间 |
| 🎁 新手礼包 | 首进自动发放：铁甲套 + 精准采集铁镐 + 岩浆/水桶 + 牛羊猪鸡蛋 + 甘蔗 + 树苗 + 泥土 |
| 🛡️ 出生点保护 | 出生点 100 格内不能破坏/放置（管理员豁免） |
| 🚪 进服自动传送 | 玩家一进服自动到钻石大陆；死亡后重生也回钻石大陆 |
| 📋 全面启动日志 | 创建世界、生成要塞全程清晰可见 |

**核心特点：零依赖、无前置插件，装进 plugins/ 点启动就全自动（自动建世界、自动生成要塞）。**

---

## 安装

1. 下载 `diamondcontinent-2.0.0.jar`
2. 放入服务器 `plugins/` 目录
3. 重启服务器（或 `/reload`）

启动后控制台出现 TinyAII 像素字横幅 + 完整启动日志即加载成功。

## 命令

| 命令 | 说明 |
| --- | --- |
| `/钻石大陆` 或 `/diamondcontinent` | 查看插件信息 |
| `/钻石大陆 传送`（`/dc tp`） | 传送到钻石大陆 |

> 别名 Alias：`/dc`、`/钻石大陆`

## 配置

无需配置即可运行（默认参数已内置），当前无独立 config.yml，所有核心功能默认开启。

## 兼容

- **Paper 1.21.8**（及其下游 Purpur / Leaves 1.21.8）
- Java 21
- 零依赖（无前置插件）

## 常见问题

- **出生后在哪？** 进服自动传送到钻石大陆出生点，脚下就是钻石矿。
- **要塞在哪？** 每 30000 格一个，代码生成的地下迷宫，含末地传送门房间。

---

# DiamondContinent (English)

Turn your server into a flat continent made entirely of diamond ore, perfect for survival/mining servers.

## Features

- Flat continent of 200 diamond-ore layers (bedrock floor, exposed diamond on the surface)
- Multiple strongholds every 30,000 blocks (generated underground mazes with end portal rooms)
- Starter kit on first join (iron armor + Silk Touch pickaxe + lava/water buckets + spawn eggs + sugar cane + sapling + dirt)
- 100-block spawn protection (admins exempt)
- Auto-teleport to the continent on join; respawn on the continent on death
- Detailed startup logs

## Install

Drop `diamondcontinent-2.0.0.jar` into `plugins/`, restart. Auto-creates the world and strongholds. Zero dependencies, no config needed.

## Commands

`/钻石大陆` or `/diamondcontinent` (info) · `/钻石大陆 传送` (`/dc tp`) (teleport)

## Compatibility

- Paper 1.21.8 (and forks: Purpur / Leaves 1.21.8)
- Java 21
- Zero dependencies

## Author

**TinyAII** · MIT License · Zero dependencies · 免费开源
