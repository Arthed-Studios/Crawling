package me.arthed.crawling;

import me.arthed.crawling.config.CrawlingConfig;
import me.arthed.crawling.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class CrPlayer {

    private final static Crawling crawling = Crawling.getInstance();

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
        if(!Utils.canCrawl(this.player)) {
            this.stopCrawling();
            return;
        }

        this.barrierBlock = player.getLocation().getBlock();

        this.player.setSwimming(true);

        this.moveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Crawling.getInstance(), () -> {

            Block blockAbovePlayer = this.player.getLocation().add(0, 1.5, 0).getBlock();
            if(!this.barrierBlock.equals(blockAbovePlayer)) {
                Utils.revertBlockPacket(this.player, this.barrierBlock);
                this.barrierBlock = blockAbovePlayer;
                if(blockAbovePlayer.isPassable()) {
                    this.player.sendBlockChange(blockAbovePlayer.getLocation(), Utils.BARRIER_BLOCK_DATA);
                }
            }

        }, 0, 1); // runs every 1 tick

        this.canCrawlTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Crawling.getInstance(), () -> {
            if(!Utils.canCrawl(this.player)) {
                Bukkit.getScheduler().runTask(Crawling.getInstance(), this::stopCrawling);
            }
        }, 20, 20); // runs every 2 0 ticks


        // Check if toggle mode should be used

        CrawlingConfig config = Crawling.getInstance().getConfig();
        boolean hold = config.getStringList("crawling_modes").contains("HOLD");
        boolean toggle = config.getStringList("crawling_modes").contains("TOGGLE");
        if(hold && toggle) {
            Bukkit.getScheduler().runTaskLater(Crawling.getInstance(), () -> {
                this.toggleMode = !this.player.isSneaking();
            }, 10);
        }
        else {
            this.toggleMode = toggle;
        }
    }

    public void stopCrawling() {
        this.player.setSwimming(false);

        if(this.barrierBlock != null) {
            Utils.revertBlockPacket(this.player, this.barrierBlock);
        }

        if(this.moveTask != null) {
            this.moveTask.cancel();
        }
        if(this.canCrawlTask != null) {
            this.canCrawlTask.cancel();
        }

        crawling.stopCrawling(this.player);
    }

    public Boolean toggleMode() {
        return this.toggleMode;
    }

}
