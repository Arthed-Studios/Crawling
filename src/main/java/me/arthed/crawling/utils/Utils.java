package me.arthed.crawling.utils;

import me.arthed.crawling.Crawling;
import me.arthed.crawling.config.CrawlingConfig;
import me.arthed.crawling.impl.WorldGuardImplementation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {

    public static BlockData BARRIER_BLOCK_DATA = Bukkit.createBlockData(Material.BARRIER);

    private static final WorldGuardImplementation worldGuard = Crawling.getInstance().getWorldGuard();
    private static final CrawlingConfig config = Crawling.getInstance().getConfig();

    public static void revertBlockPacket(Player player, final Block block) {
        player.sendBlockChange(block.getLocation(), block.getBlockData());
        Bukkit.getScheduler().runTask(Crawling.getInstance(), () -> block.getState().update());
    }

    public static boolean canCrawl(Player player) {
        if (worldGuard != null)
            if (!worldGuard.canCrawl(player))
                return false;

        boolean isOnBlacklistedBlock = config.getMaterialList("blacklisted_blocks").contains(player.getLocation().clone().subtract(0, 0.4, 0).getBlock().getType());
        if (config.getBoolean("reverse_blocks_blacklist")) {
            if (!isOnBlacklistedBlock) {
                return false;
            }
        } else if (isOnBlacklistedBlock)
            return false;

        boolean isInBlacklistedWorld = config.getWorldList("blacklisted_worlds").contains(player.getWorld());
        if (config.getBoolean("reverse_worlds_blacklist")) {
            if (!isInBlacklistedWorld) {
                return false;
            }
        } else if (isInBlacklistedWorld)
            return false;

        return !player.isFlying() &&
                !player.getLocation().getBlock().isLiquid() &&
                player.isOnGround();
    }

    public static boolean isInFrontOfATunnel(Player player) {
        WallFace facing = WallFace.fromBlockFace(player.getFacing());

        Location location = player.getLocation().add(facing.xOffset, 1, facing.zOffset);
        Block block = location.getBlock();
        double distanceLimit = facing.distance;

        if (block.getType().isSolid() && location.subtract(0, 1, 0).getBlock().isPassable()) {
            if (facing.equals(WallFace.EAST) || facing.equals(WallFace.WEST)) {
                return Math.abs(location.getX() - block.getX()) < distanceLimit;
            }
            else {
                return Math.abs(location.getZ() - block.getZ()) < distanceLimit;
            }
        }

        return false;
    }

    public enum WallFace {

        NORTH(0, -1, 0.31),
        SOUTH(0, 1, 0.70),
        WEST(-1, 0, 0.31),
        EAST(1, 0, 0.70);


        public final int xOffset;
        public final int zOffset;

        public final double distance;

        WallFace(int xOffset, int zOffset, double distance) {
            this.xOffset = xOffset;
            this.zOffset = zOffset;
            this.distance = distance;
        }

        public static WallFace fromBlockFace(BlockFace blockFace) {
            switch (blockFace) {
                case NORTH:
                    return NORTH;
                case SOUTH:
                    return SOUTH;
                case WEST:
                    return WEST;
                default: //EAST
                    return EAST;
            }
        }
    }
}
