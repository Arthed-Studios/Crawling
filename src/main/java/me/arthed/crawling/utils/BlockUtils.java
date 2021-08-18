package me.arthed.crawling.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.arthed.crawling.Crawling;

public class BlockUtils {

    public static void revertBlockPacket(Player player, final Block block) {
        player.sendBlockChange(block.getLocation(), block.getBlockData());
        Bukkit.getScheduler().runTask(Crawling.getInstance(), () -> block.getState().update());
    }

}
