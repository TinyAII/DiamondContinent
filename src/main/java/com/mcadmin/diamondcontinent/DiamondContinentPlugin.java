package com.mcadmin.diamondcontinent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 钻石大陆插件。
 *
 * 关键架构（吸取之前踩坑教训）：
 * - 保持 server.properties 的 level-name=world（不让服务器默认抢先建普通地形钻石大陆）。
 * - 插件 onEnable 主动用"超平坦 + 200 层钻石矿 + 底部基岩"生成器创建 diamond_continent。
 * - 若钻石大陆已存在但不是插件创建（被服务器默认建过），删除重建。
 * - 玩家进服自动传送到钻石大陆。
 * - 多要塞每 30000 格一个（代码生成类原版地下迷宫，含末地传送门房间）。
 * - 新手礼包、出生点 100 格保护。
 */
public final class DiamondContinentPlugin extends JavaPlugin implements Listener {

    private File dataFile;
    private YamlConfiguration data;

    @Override
    public void onEnable() {
        getLogger().info("========== DiamondContinent 钻石大陆 开始加载 ==========");

        dataFile = new File(getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);
        getLogger().info("已加载数据文件 data.yml");

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("diamondcontinent").setExecutor(this);
        getLogger().info("已注册事件监听与 /钻石大陆 命令");

        // 主动创建/加载钻石大陆（用我们的超平坦钻石生成器）
        getLogger().info("【第 1 步 / 共 2 步】正在创建/加载钻石大陆世界...");
        final World world = loadOrCreateWorld();
        if (world == null) {
            getLogger().warning("钻石大陆世界生成失败！");
        } else {
            getLogger().info("✔ 钻石大陆世界就绪：" + world.getName()
                    + "（地表高度 " + DiamondWorldGenerator.SURFACE_Y + "，" + world.getSpawnLocation().getX() + "," + world.getSpawnLocation().getY() + "," + world.getSpawnLocation().getZ() + "）");

            getLogger().info("【第 2 步 / 共 2 步】正在准备钻石要塞（每 " + DiamondWorldGenerator.STRONGHOLD_SPACING + " 格一个）...");
            // 首次：生成要塞网格（每 30000 格一个）
            if (!data.getBoolean("strongholds.generated", false)) {
                generateStrongholds(world);
            } else {
                getLogger().info("要塞已在之前生成过（strongholds.generated=true），跳过。");
            }
        }
        getLogger().info("========== DiamondContinent 钻石大陆 加载完成 ==========");

        // 品牌横幅
        String banner = """
                 _____ _                _    ___ ___
                |_   _(_)_ __  _   _   / \\  |_ _|_ _|
                  | | | | '_ \\| | | | / _ \\  | | | |
                  | | | | | | | |_| |/ ___ \\ | | | |
                  |_| |_|_| |_|\\__, /_/   \\_\\___|___|
                               |___/
                """;
        banner.lines().forEach(line -> getLogger().info(line));
        getLogger().info("DiamondContinent 钻石大陆 v" + getDescription().getVersion() + " - TinyAII 出品");
        getLogger().info("钻石大陆: " + DiamondWorldGenerator.WORLD_NAME + "（超平坦 200 格全钻石矿 + 多要塞）进服自动传送");
    }

    @Override
    public void onDisable() {
        saveData();
    }

    /** 创建/加载钻石大陆（确保用我们的超平坦生成器）。 */
    private World loadOrCreateWorld() {
        World world = Bukkit.getWorld(DiamondWorldGenerator.WORLD_NAME);
        if (world != null) {
            getLogger().info("钻石大陆已在内存中，直接使用。");
            return world;
        }
        if (DiamondWorldGenerator.exists()) {
            boolean ours = data.getBoolean("dc.created", false);
            if (!ours) {
                getLogger().warning("检测到非插件创建的 diamond_continent（可能是曾被服务器默认生成），删除重建为超平坦钻石大陆...");
                deleteWorldFolder();
            } else {
                getLogger().info("检测到插件创建的钻石大陆存档，找到 level.dat，加载中...");
                return DiamondWorldGenerator.createWorld();
            }
        } else {
            getLogger().info("未找到钻石大陆存档（无 level.dat），本次将全新创建。");
        }
        getLogger().info("正在用超平坦生成器创建钻石大陆世界（底部基岩 + 200 层全钻石矿）...");
        getLogger().info("生成参数：WorldType.FLAT，层序[底部基岩 1 层 + 钻石矿 200 层]，生物群系 plains，开启结构生成");
        World created = DiamondWorldGenerator.createWorld();
        if (created != null) {
            getLogger().info("✔ 钻石大陆世界创建完成！地表直接露钻石矿，往下 200 格全矿。");
            data.set("dc.created", true);
            saveData();
        } else {
            getLogger().warning("✘ 钻石大陆世界创建失败（createWorld 返回 null）。");
        }
        return created;
    }

    private void deleteWorldFolder() {
        World w = Bukkit.getWorld(DiamondWorldGenerator.WORLD_NAME);
        if (w != null) Bukkit.unloadWorld(w, false);
        try {
            java.nio.file.Files.walk(java.nio.file.Paths.get(DiamondWorldGenerator.WORLD_NAME))
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> { try { java.nio.file.Files.deleteIfExists(p); } catch (Exception ignored) {} });
            getLogger().info("旧 diamond_continent 已删除。");
        } catch (Exception e) {
            getLogger().warning("删除旧世界失败：" + e.getMessage());
        }
    }

    /** 网格间距生成多要塞（每 30000 格一个），异步分批防卡服。 */
    private void generateStrongholds(World world) {
        if (data.getBoolean("strongholds.generated", false)) return;
        int spacing = DiamondWorldGenerator.STRONGHOLD_SPACING;
        int range = 3; // 每个方向 3 格网格（7×7=49 个，跳过出生点网格）
        List<int[]> points = new ArrayList<>();
        for (int gx = -range; gx <= range; gx++) {
            for (int gz = -range; gz <= range; gz++) {
                if (gx == 0 && gz == 0) continue; // 出生点保护区
                long seed = (long) gx * 0x9E3779B97F4A7C15L ^ (long) gz * 0x9E3779B97F4A7C15L * 31L;
                points.add(new int[]{gx * spacing, gz * spacing, (int) seed});
            }
        }
        getLogger().info("正在生成钻石要塞网格：每 " + spacing + " 格一个，共 " + points.size() + " 个（跳过出生点）");
        final Iterator<int[]> it = points.iterator();
        final int[] done = {0};
        getServer().getScheduler().runTaskTimer(this, task -> {
            if (!it.hasNext()) {
                data.set("strongholds.generated", true);
                saveData();
                getLogger().info("✔ 全部 " + done[0] + " 个钻石要塞生成完成。");
                task.cancel();
                return;
            }
            int[] p = it.next();
            done[0]++;
            try {
                new DiamondStronghold(world, p[0], DiamondWorldGenerator.SURFACE_Y, p[1], p[2]).generate();
                getLogger().info("  已生成要塞 #" + done[0] + "/" + points.size() + " @(" + p[0] + ", " + p[1] + ")（含末地传送门房间）");
            } catch (Exception ex) {
                getLogger().warning("  要塞 #" + done[0] + " 生成失败 @(" + p[0] + "," + p[1] + ")：" + ex.getMessage());
            }
        }, 10L, 1L);
    }

    /** 保存 data.yml。 */
    public void saveData() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdirs();
            data.save(dataFile);
        } catch (IOException e) {
            getLogger().warning("保存数据失败：" + e.getMessage());
        }
    }

    // ---------- 新手礼包 ----------

    private void giveStarterKit(Player p) {
        String path = "kits." + p.getUniqueId();
        if (data.getBoolean(path, false)) return; // 已发过
        data.set(path, true);
        p.getInventory().addItem(new ItemStack(Material.IRON_HELMET));
        p.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE));
        p.getInventory().addItem(new ItemStack(Material.IRON_LEGGINGS));
        p.getInventory().addItem(new ItemStack(Material.IRON_BOOTS));
        p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 2));
        p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 2));
        p.getInventory().addItem(new ItemStack(Material.COW_SPAWN_EGG, 2));
        p.getInventory().addItem(new ItemStack(Material.SHEEP_SPAWN_EGG, 2));
        p.getInventory().addItem(new ItemStack(Material.PIG_SPAWN_EGG, 2));
        p.getInventory().addItem(new ItemStack(Material.CHICKEN_SPAWN_EGG, 2));
        p.getInventory().addItem(new ItemStack(Material.SUGAR_CANE, 1));
        ItemStack pick = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = pick.getItemMeta();
        meta.addEnchant(org.bukkit.enchantments.Enchantment.SILK_TOUCH, 1, true);
        pick.setItemMeta(meta);
        p.getInventory().addItem(pick);
        p.getInventory().addItem(new ItemStack(Material.OAK_SAPLING));
        p.getInventory().addItem(new ItemStack(Material.DIRT));
        p.sendMessage(ChatColor.GOLD + "⚒ 欢迎来到钻石大陆！新手礼包已发放");
    }

    // ---------- 事件 ----------

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        getLogger().info("玩家 " + p.getName() + " 进入服务器，检查新手礼包与传送钻石大陆...");
        giveStarterKit(p);
        // 进服自动传送到钻石大陆
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (p.isOnline() && !p.getWorld().getName().equals(DiamondWorldGenerator.WORLD_NAME)) {
                getLogger().info("玩家 " + p.getName() + " 当前在 " + p.getWorld().getName() + "，自动传送到钻石大陆");
                teleportToContinent(p);
            }
        }, 20L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        // 玩家死亡后重生在钻石大陆（不回普通世界）
        World world = Bukkit.getWorld(DiamondWorldGenerator.WORLD_NAME);
        if (world == null) return;
        // 若是带着床重生点的玩家（床在钻石大陆），保留；否则强制送到钻石大陆出生点
        if (e.getRespawnLocation() != null
                && e.getPlayer().getBedSpawnLocation() != null
                && e.getPlayer().getBedSpawnLocation().getWorld().getName().equals(DiamondWorldGenerator.WORLD_NAME)) {
            return; // 床在钻石大陆，保留原样
        }
        Location spawn = world.getSpawnLocation().clone();
        spawn.setY(DiamondWorldGenerator.safeY());
        e.setRespawnLocation(spawn);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (isInProtectedSpawn(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "钻石大陆出生点 100 格内受保护，不能破坏。");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (isInProtectedSpawn(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "钻石大陆出生点 100 格内受保护，不能放置。");
        }
    }

    /** 判断是否在出生点保护区。 */
    private boolean isInProtectedSpawn(Player p) {
        if (!p.getWorld().getName().equals(DiamondWorldGenerator.WORLD_NAME)) return false;
        if (p.hasPermission("diamondcontinent.admin")) return false;
        World w = p.getWorld();
        Location spawn = w.getSpawnLocation();
        return spawn.distance(p.getLocation()) <= DiamondWorldGenerator.SPAWN_PROTECT_RADIUS;
    }

    // ---------- 命令 ----------

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendInfo(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("传送") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("go")) {
            if (sender instanceof Player p) teleportToContinent(p);
            else sender.sendMessage(ChatColor.RED + "只有玩家能传送。");
            return true;
        }
        sendInfo(sender);
        return true;
    }

    private void sendInfo(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "===== 钻石大陆 =====");
        s.sendMessage(ChatColor.WHITE + "超平坦 200 格全钻石矿世界 + 多要塞");
        s.sendMessage(ChatColor.YELLOW + "/钻石大陆 传送" + ChatColor.WHITE + "  进入钻石大陆");
    }

    private void teleportToContinent(Player p) {
        World world = Bukkit.getWorld(DiamondWorldGenerator.WORLD_NAME);
        if (world == null) {
            getLogger().warning("玩家 " + p.getName() + " 请求传送，但钻石大陆世界未生成！");
            p.sendMessage(ChatColor.RED + "钻石大陆世界未生成。");
            return;
        }
        Location spawn = world.getSpawnLocation();
        spawn.setY(DiamondWorldGenerator.safeY());
        p.teleport(spawn);
        getLogger().info("✔ 玩家 " + p.getName() + " 已传送到钻石大陆出生点 @(" +
                (int) spawn.getX() + "," + (int) spawn.getY() + "," + (int) spawn.getZ() + ")");
        p.sendMessage(ChatColor.GOLD + "💎 你已来到钻石大陆！脚下就是钻石矿，尽情挖吧！");
    }
}
