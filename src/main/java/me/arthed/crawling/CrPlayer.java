package me.arthed.crawling;

import me.arthed.crawling.config.CrawlingConfig;
import me.arthed.crawling.utils.BlockUtils;
import me.arthed.crawling.utils.Utils;
import me.arthed.nms.NmsPackets;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CrPlayer {

    private final static Crawling crawling = Crawling.getInstance();
    private final static NmsPackets nmsPacketManager = crawling.getNmsPacketManager();

    private final Player player;

    private Block barrierBlock;

    private BukkitTask moveTask; // task running every 3 ticks making sure the barrier is above the player
    private BukkitTask canCrawlTask; // task running every 20 ticks checking if the player can continue crawling

    private Boolean toggleMode;

    protected CrPlayer(Player player) {
        this.player = player;
        startCrawling();
    }

    public void startCrawling() {
        if (!Utils.canCrawl(this.player)) {
            this.stopCrawling();
            return;
        }

        this.barrierBlock = player.getLocation().getBlock();

        this.player.setSwimming(true);

        this.moveTask = Bukkit.getScheduler().runTaskTimer(Crawling.getInstance(), () -> {

            Block blockAbovePlayer = this.player.getLocation().add(0, 1.5, 0).getBlock();
            if (!this.barrierBlock.equals(blockAbovePlayer)) {
                this.replaceBarrier(blockAbovePlayer);
            }

        }, 0, 1); // runs every tick

        this.canCrawlTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Crawling.getInstance(), () -> {
            if (!Utils.canCrawl(this.player)) {
                Bukkit.getScheduler().runTask(Crawling.getInstance(), this::stopCrawling);
            } else if (this.player.getVelocity().getY() > 0 && this.player.getNoDamageTicks() == 0)
                Bukkit.getScheduler().runTask(Crawling.getInstance(), this::stopCrawling);
        }, 20, 20); // runs every 20 ticks


        // Check if toggle mode should be used

        CrawlingConfig config = Crawling.getInstance().getConfig();
        boolean hold = config.getStringList("crawling_modes").contains("HOLD");
        boolean toggle = config.getStringList("crawling_modes").contains("TOGGLE");
        if (hold && toggle) {
            Bukkit.getScheduler().runTaskLater(Crawling.getInstance(), () -> this.toggleMode = !this.player.isSneaking(), 10);
        } else {
            this.toggleMode = toggle;
        }
    }

    public void replaceBarrier(Block blockAbovePlayer) {
        Utils.revertBlockPacket(this.player, this.barrierBlock);
        Utils.revertBlockPacket(this.player, this.barrierBlock.getLocation().subtract(0, 2, 0).getBlock());
        nmsPacketManager.removeFakeBlocks(this.player);
        this.barrierBlock = blockAbovePlayer;
        if (blockAbovePlayer.isPassable() || BlockUtils.nonFullBlocks.contains(blockAbovePlayer.getType())) {
            this.player.sendBlockChange(blockAbovePlayer.getLocation(), Utils.BARRIER_BLOCK_DATA);
            if (!blockAbovePlayer.getType().isAir()) {
                Block floorBlock = blockAbovePlayer.getLocation().subtract(0, 2, 0).getBlock();
                nmsPacketManager.spawnFakeBlocks(this.player, blockAbovePlayer, floorBlock, BlockUtils.similarSoundBlocks.get(floorBlock.getType()));
            }
        }
    }

    public void stopCrawling() {
        this.player.setSwimming(false);

        if (this.barrierBlock != null) {
            Utils.revertBlockPacket(this.player, this.barrierBlock);
            Utils.revertBlockPacket(this.player, this.barrierBlock.getLocation().subtract(0, 2, 0).getBlock());
            nmsPacketManager.removeFakeBlocks(this.player);
        }

        if (this.moveTask != null) {
            this.moveTask.cancel();
        }
        if (this.canCrawlTask != null) {
            this.canCrawlTask.cancel();
        }

        crawling.stopCrawling(this.player);
    }

    public Boolean toggleMode() {
        return this.toggleMode;
    }

}
