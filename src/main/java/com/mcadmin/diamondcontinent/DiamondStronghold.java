package com.mcadmin.diamondcontinent;

import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

/**
 * 钻石要塞生成器：在钻石大陆的地下生成类似原版要塞的地下迷宫。
 *
 * 每个要塞：地表入口竖井 → 主干道 → 两侧随机房间（图书馆/刷怪房/宝藏房）→ 末端末地传送门房间。
 * 风格接近原版要塞（石砖迷宫 + 末地传送门），用代码生成（超平坦钻石大陆无法用原版要塞生成器）。
 */
public final class DiamondStronghold {

    private final World world;
    private final int entX, entZ, entY;      // 入口地表
    private final int depth;                 // 要塞主体地下深度
    private final Random random;

    public DiamondStronghold(World world, int x, int y, int z, long seed) {
        this.world = world;
        this.entX = x;
        this.entY = y;
        this.entZ = z;
        this.depth = 24;
        this.random = new Random(seed);
    }

    /** 生成要塞（主线程调用）。 */
    public void generate() {
        int mainY = entY - depth; // 主干道高度
        digShaft(mainY);

        boolean eastWest = random.nextBoolean();
        int length = 30 + random.nextInt(40);
        int dx = eastWest ? 1 : 0;
        int dz = eastWest ? 0 : 1;
        digCorridor(entX, mainY, entZ, dx, dz, length);

        // 两侧房间
        for (int i = 1; i * 12 < length; i++) {
            int along = i * 12;
            int rx = entX + (eastWest ? along : random.nextInt(5) - 2);
            int rz = entZ + (eastWest ? random.nextInt(5) - 2 : along);
            digRoom(rx, rz, mainY);
            // 隧道到房间
            int tx = entX + (eastWest ? along : 0);
            int tz = entZ + (eastWest ? 0 : along);
            digTunnel(tx, mainY, tz, rx - tx, rz - tz);
        }

        // 末端末地传送门房间
        int endX = entX + (eastWest ? length : 0);
        int endZ = entZ + (eastWest ? 0 : length);
        int pcx = endX + (eastWest ? 8 : 0);
        int pcz = endZ + (eastWest ? 0 : 8);
        digPortalRoom(pcx, pcz, mainY);
        digTunnel(endX, mainY, endZ, pcx - endX, pcz - endZ);
    }

    private void setAir(int x, int y, int z) { world.getBlockAt(x, y, z).setType(Material.AIR, false); }
    private void place(Material m, int x, int y, int z) { world.getBlockAt(x, y, z).setType(m, false); }

    /** 入口竖井（地表到主体空腔）。 */
    private void digShaft(int mainY) {
        for (int y = entY; y > mainY; y--) {
            setAir(entX, y, entZ);
            setAir(entX + 1, y, entZ);
            setAir(entX, y, entZ + 1);
            setAir(entX + 1, y, entZ + 1);
        }
        // 入口周围石砖装饰
        place(Material.STONE_BRICKS, entX - 1, entY, entZ - 1);
        place(Material.STONE_BRICKS, entX + 2, entY, entZ - 1);
        place(Material.STONE_BRICKS, entX - 1, entY, entZ + 2);
        place(Material.STONE_BRICKS, entX + 2, entY, entZ + 2);
    }

    private void digCorridor(int x0, int y, int z0, int dx, int dz, int len) {
        for (int i = 0; i < len; i++) {
            int cx = x0 + i * dx;
            int cz = z0 + i * dz;
            for (int h = -1; h <= 1; h++) {
                setAir(cx, y + h, cz);
                if (dx == 0) setAir(cx + 1, y + h, cz);
                else setAir(cx, y + h, cz + 1);
            }
            place(Material.STONE_BRICKS, cx, y - 1, cz);
            if (i % 6 == 3) place(Material.TORCH, cx, y + 1, cz);
        }
    }

    private void digTunnel(int x0, int y, int z0, int dx, int dz) {
        int len = Math.max(Math.abs(dx), Math.abs(dz));
        int sx = Integer.signum(dx), sz = Integer.signum(dz);
        for (int i = 1; i < len; i++) {
            int cx = x0 + i * sx, cz = z0 + i * sz;
            for (int h = -1; h <= 1; h++) setAir(cx, y + h, cz);
        }
    }

    private void digRoom(int cx, int cz, int y) {
        for (int a = -3; a <= 4; a++) {
            for (int b = -3; b <= 4; b++) {
                for (int h = -1; h <= 2; h++) setAir(cx + a, y + h, cz + b);
                place(Material.STONE_BRICKS, cx + a, y - 2, cz + b);
            }
        }
        int type = random.nextInt(3);
        if (type == 0) library(cx, y, cz);
        else if (type == 1) spawnerRoom(cx, y, cz);
        else treasureRoom(cx, y, cz);
        place(Material.TORCH, cx - 3, y + 1, cz);
    }

    private void library(int cx, int y, int cz) {
        for (int a = -3; a <= 4; a++) {
            place(Material.BOOKSHELF, cx + a, y, cz - 3);
            place(Material.BOOKSHELF, cx + a, y + 1, cz - 3);
            place(Material.BOOKSHELF, cx + a, y, cz + 4);
            place(Material.BOOKSHELF, cx + a, y + 1, cz + 4);
        }
        place(Material.COBWEB, cx - 3, y + 2, cz - 3);
        place(Material.COBWEB, cx + 4, y + 2, cz + 4);
        place(Material.LANTERN, cx, y + 1, cz);
    }

    private void spawnerRoom(int cx, int y, int cz) {
        place(Material.SPAWNER, cx, y, cz);
        place(Material.MOSSY_COBBLESTONE, cx, y, cz - 1);
        place(Material.MOSSY_COBBLESTONE, cx, y, cz + 1);
        place(Material.TORCH, cx, y + 1, cz);
    }

    private void treasureRoom(int cx, int y, int cz) {
        place(Material.CHEST, cx - 1, y, cz);
        place(Material.CHEST, cx, y, cz);
        place(Material.LANTERN, cx, y + 1, cz);
    }

    private void digPortalRoom(int cx, int cz, int y) {
        for (int a = -5; a <= 5; a++) {
            for (int b = -5; b <= 5; b++) {
                for (int h = -1; h <= 3; h++) setAir(cx + a, y + h, cz + b);
                place(Material.STONE_BRICKS, cx + a, y - 2, cz + b);
            }
        }
        // 末地传送门框架（5x5，中间空）
        for (int a = -2; a <= 2; a++) {
            place(Material.END_PORTAL_FRAME, cx + a, y, cz - 2);
            place(Material.END_PORTAL_FRAME, cx + a, y, cz + 2);
        }
        place(Material.END_PORTAL_FRAME, cx - 2, y, cz - 1);
        place(Material.END_PORTAL_FRAME, cx - 2, y, cz);
        place(Material.END_PORTAL_FRAME, cx - 2, y, cz + 1);
        place(Material.END_PORTAL_FRAME, cx + 2, y, cz - 1);
        place(Material.END_PORTAL_FRAME, cx + 2, y, cz);
        place(Material.END_PORTAL_FRAME, cx + 2, y, cz + 1);
        // 银鱼刷怪笼 + 火把
        place(Material.SPAWNER, cx - 4, y, cz - 4);
        place(Material.TORCH, cx - 4, y + 1, cz - 4);
        place(Material.TORCH, cx + 4, y + 1, cz + 4);
    }
}
