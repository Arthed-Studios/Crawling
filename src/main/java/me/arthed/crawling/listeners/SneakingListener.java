package me.arthed.crawling.listeners;

import me.arthed.crawling.CrPlayer;
import me.arthed.crawling.Crawling;
import me.arthed.crawling.config.CrawlingConfig;
import me.arthed.crawling.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SneakingListener implements Listener {

    private final Crawling crawling = Crawling.getInstance();
    private final CrawlingConfig config = crawling.getConfig();

    private final Set<Player> doubleSneakingCheck = new HashSet<>();
    private final Map<Player, BukkitTask> holdCheck = new HashMap<>();

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if(!event.getPlayer().isOnGround()) return;
        Bukkit.getScheduler().runTaskAsynchronously(crawling, () -> {

            Player player = event.getPlayer();
            CrPlayer crPlayer = crawling.getPlayerCrawling(player);

            if(event.isSneaking()) {

                // Stop Crawling
                if(crPlayer != null && crPlayer.toggleMode() != null && crPlayer.toggleMode() && this.config.getStringList("crawling_modes").contains("TOGGLE")) {
                    Bukkit.getScheduler().runTask(crawling, crPlayer::stopCrawling);
                    return;
                }

                // Start crawling when sneaking while in a tunnel if HOLD mode is enabled
                if(crPlayer == null && player.isSwimming() && this.config.getStringList("crawling_modes").contains("HOLD") && !player.getLocation().getBlock().isLiquid()) {
                    Bukkit.getScheduler().runTask(crawling, () -> crawling.startCrawling(player));
                    return;
                }

                // Tunnels
                if (this.config.getStringList("crawling_modes").contains("TUNNELS")) {
                    if(Utils.isInFrontOfATunnel(player)) {

                        Bukkit.getScheduler().runTask(crawling, () -> crawling.startCrawling(player));

                        Utils.WallFace facing = Utils.WallFace.fromBlockFace(player.getFacing());

                        Bukkit.getScheduler().runTaskLater(this.crawling, () -> {
                            CrPlayer crPlayer1 = crawling.getPlayerCrawling(player);
                            if(crPlayer1 != null) {
                                crPlayer1.stopCrawling();
                            }
                        }, 10);
                        return;
                    }
                }

                if(player.getLocation().getPitch() > 87) { // The player is looking downwards and is not crawling
                    // Double Sneaking
                    if (this.config.getStringList("start_crawling").contains("DOUBLE_SHIFT") || this.config.getStringList("start_crawling").contains("DOWN_DOUBLE_SHIFT")) { //if double sneaking is enabled
                        if (!doubleSneakingCheck.contains(player)) {
                            doubleSneakingCheck.add(player);
                            Bukkit.getScheduler().runTaskLaterAsynchronously(crawling, () -> doubleSneakingCheck.remove(player), 8);
                        } else {
                            doubleSneakingCheck.remove(player);
                            Bukkit.getScheduler().runTask(crawling, () -> crawling.startCrawling(player));
                        }
                        return;
                    }

                    // Hold
                    for(String start_crawling : this.config.getStringList("start_crawling")) {
                        if(start_crawling.contains("HOLD")) {
                            this.holdCheck.remove(player);
                            int time = Integer.parseInt(start_crawling.split("_")[1]);
                            this.holdCheck.put(player, Bukkit.getScheduler().runTaskLater(this.crawling, () -> {
                                if(player.isSneaking() && player.getLocation().getPitch() > 87) {
                                    crawling.startCrawling(player);
                                    this.holdCheck.remove(player);
                                }
                            }, time));
                            return;
                        }
                    }
                }

            }
            else { // If the player is not sneaking

                // Stop Crawling
                if(crPlayer != null && crPlayer.toggleMode() != null && !crPlayer.toggleMode() && this.config.getStringList("crawling_modes").contains("HOLD")) {
                    Bukkit.getScheduler().runTask(crawling, crPlayer::stopCrawling);
                }
            }
        });
    }

}
