package me.arthed.crawling.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Snow;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;

import me.arthed.crawling.Crawling;
import org.bukkit.util.Vector;

public class PositionChecks {

    public static boolean isTouchingATunnel(Player p) {

        Location loc = p.getLocation();
        for(BlockWall blockWall : BlockWall.values()) {
            if(p.getFacing().equals(blockWall.blockFace)) {
                double axis = blockWall.inverseAxis ? p.getLocation().getZ() : p.getLocation().getX();
                int negativeDecimals = blockWall.inverseDistanceOnNegativeCords ? 30 : 69;
                int positiveDecimals = blockWall.inverseDistanceOnNegativeCords ? 69 : 30;
                if(axis < 0 ? (int)((axis-(int)axis)*100) != negativeDecimals : (int)((axis-(int)axis)*100) != positiveDecimals) {
                    return false;
                }
                loc.add(blockWall.locationOffset);
                break;
            }
        }

        if(canGoTrough(loc) && !loc.clone().add(0,1,0).getBlock().isPassable() && !loc.clone().subtract(0,1,0).getBlock().isPassable() && isValid(loc.clone().subtract(0,1,0))) {
            if(loc.clone().add(0,1,0).getBlock().getType().name().contains("SLAB")) {
                Slab slab = (Slab) loc.clone().add(0,1,0).getBlock().getBlockData();
                if(slab.getType().equals(Type.TOP)) {
                    return false;
                }
            }
            if(loc.clone().subtract(0,1,0).getBlock().getType().name().contains("SLAB")) {
                try {
                    Slab slab = (Slab) loc.clone().add(0,1,0).getBlock().getBlockData();
                    if(slab.getType().equals(Type.BOTTOM)) {
                        return false;
                    }
                }
                catch(Exception ignored) {}
            }
            if(loc.clone().add(0,1,0).getBlock().getType().name().contains("STAIRS")) {
                Stairs stairs = (Stairs) loc.clone().add(0,1,0).getBlock().getBlockData();
                return !stairs.getHalf().equals(Half.TOP);
            }

            return true;
        }
        return false;
    }

    public static boolean isTouchingAFence(Player p) {
        Location loc = p.getLocation();

        if(!loc.clone().add(0,1,0).getBlock().getType().name().contains("FENCE")) {
            return false;
        }

        for(BlockWall blockWall : BlockWall.values()) {
            if(p.getFacing().equals(blockWall.blockFace)) {
                double axis = blockWall.inverseAxis ? p.getLocation().getZ() : p.getLocation().getX();
                int negativeDecimals = blockWall.inverseDistanceOnNegativeCords ? 925 : 74;
                int positiveDecimals = blockWall.inverseDistanceOnNegativeCords ? 74 : 925;
                if(axis < 0 ? (int)((axis-(int)axis)*1000) != negativeDecimals : (int)((axis-(int)axis)*1000) != positiveDecimals) {
                    return false;
                }
                break;
            }
        }

        return canGoTrough(p.getLocation()) && !p.getLocation().clone().add(0, 1, 0).getBlock().isPassable() && !p.getLocation().clone().subtract(0, 1, 0).getBlock().isPassable();
    }

    public static boolean isTouchingAWall(Player p) {
        Location loc = p.getLocation();

        for(BlockWall blockWall : BlockWall.values()) {
            if(p.getFacing().equals(blockWall.blockFace)) {
                double axis = blockWall.inverseAxis ? p.getLocation().getZ() : p.getLocation().getX();
                int negativeDecimals = blockWall.inverseDistanceOnNegativeCords ? 50 : 949;
                int positiveDecimals = blockWall.inverseDistanceOnNegativeCords ? 949 : 50;
                if(axis < 0 ? (int)((axis-(int)axis)*1000) != negativeDecimals : (int)((axis-(int)axis)*1000) != positiveDecimals) {
                    return false;
                }
                loc.add(blockWall.locationOffset);
                break;
            }
        }

        return canGoTrough(loc) && !loc.clone().add(0, 1, 0).getBlock().isPassable() && !loc.clone().subtract(0, 1, 0).getBlock().isPassable() && loc.clone().add(0, 1, 0).getBlock().getType().name().contains("WALL");
    }


    public static boolean canGoTrough(Location loc) {
        return loc.getBlock().isPassable() ||
                loc.getBlock().getType().name().contains("CARPET") ||
                loc.getBlock().getType().name().contains("TRAPDOOR") ||
                loc.getBlock().getType().name().contains("TRAP_DOOR") ||
                loc.getBlock().getType().equals(Material.REPEATER) ||
                loc.getBlock().getType().equals(Material.COMPARATOR) ||
                loc.getBlock().getType().equals(Material.SCAFFOLDING) ||
                (loc.getBlock().getType().equals(Material.SNOW) && ((Snow)loc.getBlock().getBlockData()).getLayers() < 5);
    }

    public static boolean isValid(Location loc) {
        return !Crawling.getInstance().config.getMaterialList("blacklisted_blocks").contains(loc.getBlock().getType());
    }

    private enum BlockWall {
        NORTH(BlockFace.NORTH, true, false, new Vector(0, 0, -1)),
        SOUTH(BlockFace.SOUTH, true, true, new Vector(0, 0, 1)),
        WEST(BlockFace.WEST, false, false, new Vector(-1, 0, 0)),
        EAST(BlockFace.EAST, false, true, new Vector(1, 0, 0));

        public final BlockFace blockFace;
        public final boolean inverseAxis;
        public final boolean inverseDistanceOnNegativeCords;
        public final Vector locationOffset;

        BlockWall(BlockFace blockFace, boolean inverseAxis, boolean inverseDistanceOnNegativeCords, Vector locationOffset) {
            this.blockFace = blockFace;
            this.inverseAxis = inverseAxis;
            this.inverseDistanceOnNegativeCords = inverseDistanceOnNegativeCords;
            this.locationOffset = locationOffset;
        }
    }

}