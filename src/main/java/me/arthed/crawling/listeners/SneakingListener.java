package me.arthed.crawling.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import me.arthed.crawling.Crawling;
import me.arthed.crawling.players.CrPlayer;
import me.arthed.crawling.utils.PositionChecks;

public class SneakingListener implements Listener {

    private final ArrayList<Player> doubleSneakCheck = new ArrayList<>();
    private final HashMap<Player, Runnable> holdSneakCheck = new HashMap<>();

    private final Crawling plugin;

    public SneakingListener() {
        plugin = Crawling.getInstance();
    }


    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        if(plugin.config.getBoolean("need_permission_to_crawl"))
            if(!e.getPlayer().hasPermission("crawl.player") && !e.getPlayer().hasPermission("crawling.player"))
                return;

        Player player = e.getPlayer();
        if(e.isSneaking()) {
            if(!player.isSneaking()) {
                CrPlayer crPlayer = plugin.playerManager.getCrPlayer(e.getPlayer());
                if(crPlayer.isCrawling() && crPlayer.toggleModeCrawling) {
                    crPlayer.stopCrawling();
                    return;
                }

            }
            List<String> crawling_modes = plugin.config.getStringList("crawling_modes");
            if(crawling_modes.contains("HOLD") || crawling_modes.contains("TOGGLE")) {
                List<String> start_crawling = plugin.config.getStringList("start_crawling");
                //double shift
                if(start_crawling.contains("DOUBLE_SHIFT") || start_crawling.contains("DOWN_DOUBLE_SHIFT")) {
                    if(!doubleSneakCheck.contains(player)) {
                        doubleSneakCheck.add(player);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Crawling.getInstance(), () -> doubleSneakCheck.remove(player), 10);
                    }
                    else {
                        CrPlayer crPlayer = plugin.playerManager.getCrPlayer(player);
                        if(start_crawling.contains("DOWN_DOUBLE_SHIFT")) {
                            if(player.getLocation().getPitch() > 87) {
                                crPlayer.startCrawling();
                                return;
                            }
                        }
                        else {
                            crPlayer.startCrawling();
                            return;
                        }
                    }
                }

                //hold to start
                holdSneakCheck.remove(player);

                if(player.getLocation().getPitch() > 87) {
                    for(String start_crawling_option : start_crawling) {
                        if(start_crawling_option.startsWith("HOLD")) {
                            double amount = Double.parseDouble(start_crawling_option.split("_")[1]);

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if(player.isSneaking() &&
                                            player.getLocation().getPitch() > 87 &&
                                            holdSneakCheck.containsKey(player) &&
                                            holdSneakCheck.get(player).equals(this)) {

                                        plugin.playerManager.getCrPlayer(player).startCrawling();
                                        return;
                                    }
                                    holdSneakCheck.remove(player);
                                }
                            };
                            holdSneakCheck.put(player, runnable);
                            Bukkit.getScheduler().runTaskLater(plugin, runnable, (long) (amount * 20));
                        }
                    }
                }

            }
            if(crawling_modes.contains("TUNNELS")) {
                if(PositionChecks.isTouchingATunnel(player) || PositionChecks.isTouchingAFence(player) || PositionChecks.isTouchingAWall(player)) {
                    plugin.playerManager.getCrPlayer(player).startCrawling();
                }
            }
        }
        else {
            if(player.isSneaking()) {
                CrPlayer crPlayer = plugin.playerManager.getCrPlayer(e.getPlayer());
                if(crPlayer.isCrawling() && !crPlayer.toggleModeCrawling) {
                    crPlayer.stopCrawling();
                }
            }
        }

    }

}
