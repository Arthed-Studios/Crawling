package me.arthed.crawling.listeners;

import me.arthed.crawling.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.arthed.crawling.Crawling;

public class PlayerInteractListener implements Listener {

    private final Crawling crawling = Crawling.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(crawling, () -> {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (event.getClickedBlock() != null) {
                    if (event.getClickedBlock().isPassable() && event.getClickedBlock().equals(event.getPlayer().getLocation().add(0, 1.5, 0).getBlock())) {
                        if (crawling.isCrawling(event.getPlayer())) {
                            event.setCancelled(true);
                            event.getPlayer().sendBlockChange(event.getClickedBlock().getLocation(), Utils.BARRIER_BLOCK_DATA);
                        }
                    }
                }
            }
        });
    }

}
