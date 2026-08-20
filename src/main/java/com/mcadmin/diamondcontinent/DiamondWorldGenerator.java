package com.mcadmin.diamondcontinent;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/**
 * 钻石大陆世界生成器。
 *
 * 用超平坦生成器直接铺出"底部基岩 + 200 层 diamond_ore"的世界：
 * 地表直接露钻石矿、往下 200 格全钻石矿、底部基岩。这样世界生成即钻石大陆，不依赖代码填充。
 *
 * 关键（之前踩坑学到的）：
 * - 超平坦 layers 第一个 = 最底部(bedrock)，往上堆叠。bottom bedrock1 + diamond_ore200 → 顶面 = -64+200 = 136。
 * - 不要在 server.properties 改 level-name=diamond_continent（会让服务器抢先默认建普通世界），
 *   保持主世界 world，由插件主动创建钻石大陆，玩家进服传送到它。
 */
public final class DiamondWorldGenerator {

    public static final String WORLD_NAME = "diamond_continent";

    /** 要塞间距（格）。 */
    public static int STRONGHOLD_SPACING = 30000;

    /** 出生点保护区半径（格）。 */
    public static int SPAWN_PROTECT_RADIUS = 100;

    /** 钻石平原地表高度（超平坦 bottom bedrock1 + 200 层 diamond_ore：顶面 = -64 + 200 = 136）。 */
    public static int SURFACE_Y = 136;

    private DiamondWorldGenerator() {}

    /** 检测世界是否已存在。 */
    public static boolean exists() {
        return org.bukkit.Bukkit.getWorld(WORLD_NAME) != null
                || new java.io.File(WORLD_NAME, "level.dat").exists();
    }

    /** 创建钻石大陆世界（超平坦 + 200 层钻石矿 + 底部基岩）。 */
    public static World createWorld() {
        WorldCreator wc = new WorldCreator(WORLD_NAME);
        wc.type(WorldType.FLAT);
        // layers 第一个=最底部。bottom bedrock1 + 200 层 diamond_ore → 地表直接露钻石矿，往下 200 格全矿。
        wc.generatorSettings("{\"layers\":["
                + "{\"block\":\"minecraft:bedrock\",\"height\":1},"
                + "{\"block\":\"minecraft:diamond_ore\",\"height\":200}"
                + "],\"biome\":\"minecraft:plains\"}");
        wc.generateStructures(true); // 保留村庄/林地府邸/废弃传送门/要塞等建筑遗迹
        wc.environment(World.Environment.NORMAL);
        World world = wc.createWorld();
        if (world == null) return null;
        return world;
    }

    /** 钻石平原地表安全 Y（站在钻石矿上）。 */
    public static int safeY() {
        return SURFACE_Y + 2;
    }
}
