package me.arthed.nms;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface NmsPackets {

    void spawnFakeBlocks(Player player, Block block, Block floorBlock, Material fakeFloorMaterial);
    void removeFakeBlocks(Player player);

}
