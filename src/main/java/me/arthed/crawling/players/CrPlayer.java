package me.arthed.crawling.players;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import me.arthed.crawling.Crawling;
import me.arthed.crawling.utils.BlockUtils;

public class CrPlayer {

    private static Crawling plugin;

    private final Player player;

    private final List<Block> barrierBlocks = new ArrayList<>();

    private boolean isCrawling;
    public boolean toggleModeCrawling;

    private BukkitTask updateTask;

    protected CrPlayer(Player player) {
        this.player = player;

        if(plugin == null)
            plugin = Crawling.getInstance();
    }

    public boolean isCrawling() {
        return this.isCrawling;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void startCrawling() {

        if(!canCrawl())
            return;

        this.isCrawling = true;

        this.player.setSwimming(true);

        //stop players from jumping while crawling
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, 100000, false, false, false));

        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if(!canCrawl()) {
                Bukkit.getScheduler().runTask(plugin, this::stopCrawling);
            }
            else if(!player.isSneaking()) {
                if(!toggleModeCrawling && player.getOpenInventory().getType().equals(InventoryType.CREATIVE) ||
                        player.getOpenInventory().getType().equals(InventoryType.CRAFTING)) {
                    Bukkit.getScheduler().runTask(plugin, this::stopCrawling);
                }
            }
        }, 0, 20);

        //handle HOLD and TOGGLE crawling modes
        this.toggleModeCrawling = true;
        List<String> crawlingModes = plugin.config.getStringList("crawling_modes");
        if(crawlingModes.contains("TOGGLE")) {
            if(crawlingModes.contains("HOLD")) {
                //if the player releases shift right after starting crawling, activate toggle
                Bukkit.getScheduler().runTaskLater(plugin, () -> toggleModeCrawling = !player.isSneaking(), 10);
            }
            else {
                this.toggleModeCrawling = true;
            }
        }
        else if(crawlingModes.contains("HOLD"))
            this.toggleModeCrawling = false;

        this.addBarrierAbove(this.player.getLocation().clone().add(0, 1.9, 0).getBlock());
    }


    public void onMove(PlayerMoveEvent event) {
        if(this.isCrawling) {
            Block barrier = Objects.requireNonNull(event.getTo()).clone().add(0, 1.9, 0).getBlock();
            if(!this.barrierBlocks.contains(barrier) && barrier.isPassable()) {
                this.addBarrierAbove(barrier);
            }
        }
    }

    public void addBarrierAbove(Block barrier) {
        this.barrierBlocks.add(barrier);
        this.player.sendBlockChange(barrier.getLocation(), Bukkit.createBlockData(Material.BARRIER));
        List<Block> toRemove = new ArrayList<>();
        try {
            for(Block block : this.barrierBlocks) {
                if(block != null) {
                    if (!block.equals(barrier)) {
                        BlockUtils.revertBlockPacket(this.player, block);
                        toRemove.add(block);
                    }
                }
            }
        }
        catch(ConcurrentModificationException ignore) {}
        for(Block block : toRemove) {
            this.barrierBlocks.remove(block);
        }
    }


    public void stopCrawling() {
        this.isCrawling = false;

        player.setSwimming(false);

        updateTask.cancel();

        for(Block barrier : barrierBlocks) {
            BlockUtils.revertBlockPacket(player, barrier);
        }
        barrierBlocks.clear();

        player.removePotionEffect(PotionEffectType.JUMP);
    }

    private boolean canCrawl() {
/*        if(plugin.worldGuard != null)
            if(!plugin.worldGuard.canCrawl(player))
                return false;*/

        boolean isOnBlacklistedBlock = plugin.config.getMaterialList("blacklisted_blocks").contains(player.getLocation().clone().subtract(0, 0.4, 0).getBlock().getType());
        if(plugin.config.getBoolean("reverse_blocks_blacklist")) {
            if (!isOnBlacklistedBlock) {
                return false;
            }
        }
        else if(isOnBlacklistedBlock)
            return false;

        boolean isInBlacklistedWorld = plugin.config.getWorldList("blacklisted_worlds").contains(player.getWorld());
        if(plugin.config.getBoolean("reverse_worlds_blacklist")) {
            if (!isInBlacklistedWorld) {
                return false;
            }
        }
        else if(isInBlacklistedWorld)
            return false;

        List<String> crawling_modes = plugin.config.getStringList("crawling_modes");
        if(!crawling_modes.contains("HOLD") &&
                !crawling_modes.contains("TOGGLE") &&
                player.getLocation().clone().add(0, 1.25, 0).getBlock().isPassable())
            return false;

        return !player.isFlying() &&
                !player.getLocation().getBlock().isLiquid();
    }

}
