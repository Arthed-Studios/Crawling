package me.arthed.crawling.nms.v1_16;

import me.arthed.nms.NmsPackets;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NmsPackets_v1_16 implements NmsPackets {

    private final int blockId = -8854;
    private final int floorBlockId = -8855;
    private final DataWatcher dataWatcher;

    public NmsPackets_v1_16() {
        World world = Bukkit.getWorlds().get(0);
        FallingBlock fallingBlock = (FallingBlock) world.spawnEntity(new Location(world, 0, 0, 0), EntityType.FALLING_BLOCK);
        fallingBlock.setGravity(false);
        this.dataWatcher = ((CraftEntity)fallingBlock).getHandle().getDataWatcher();
        fallingBlock.remove();
    }

    public void spawnFakeBlocks(Player player, Block block, Block floorBlock, Material fakeFloorMaterial) {
        PacketPlayOutSpawnEntity spawnBlockPacket = new PacketPlayOutSpawnEntity(
                this.blockId,
                UUID.randomUUID(), // entity uuid
                block.getX() + 0.5,
                block.getY(),
                block.getZ() + 0.5,
                0, //yaw
                0, //pitch
                EntityTypes.FALLING_BLOCK,
                net.minecraft.server.v1_16_R3.Block.getCombinedId(((CraftBlock)block).getNMS()), //material id
                new Vec3D(0, 1, 0) // velocity
        );
        PacketPlayOutEntityMetadata blockMetadataPacket = new PacketPlayOutEntityMetadata(this.blockId, this.dataWatcher, true);
        PacketPlayOutSpawnEntity spawnBlockPacket2 = new PacketPlayOutSpawnEntity(
                this.floorBlockId,
                UUID.randomUUID(), // entity uuid
                floorBlock.getX() + 0.5,
                floorBlock.getY() + 0.001f,
                floorBlock.getZ() + 0.5,
                90, //yaw
                0, //pitch
                EntityTypes.FALLING_BLOCK,
                net.minecraft.server.v1_16_R3.Block.getCombinedId(((CraftBlock)floorBlock).getNMS()), //material id
                new Vec3D(0, 1, 0) // velocity
        );
        PacketPlayOutEntityMetadata blockMetadataPacket2 = new PacketPlayOutEntityMetadata(this.floorBlockId, this.dataWatcher, true);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawnBlockPacket);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(blockMetadataPacket);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawnBlockPacket2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(blockMetadataPacket2);
        player.sendBlockChange(floorBlock.getLocation(), fakeFloorMaterial == null ? Bukkit.createBlockData(Material.STONE) : Bukkit.createBlockData(fakeFloorMaterial));
    }

    public void removeFakeBlocks(Player player) {
        PacketPlayOutEntityDestroy destroyOldBlockPacket = new PacketPlayOutEntityDestroy(this.blockId);
        PacketPlayOutEntityDestroy destroyOldBlockPacket2 = new PacketPlayOutEntityDestroy(this.blockId-1);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyOldBlockPacket);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyOldBlockPacket2);
    }


}
