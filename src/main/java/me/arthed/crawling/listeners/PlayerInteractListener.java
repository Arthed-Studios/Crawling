package me.arthed.crawling.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.arthed.crawling.Crawling;
import me.arthed.crawling.players.CrPlayer;

import java.util.Objects;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if(Objects.requireNonNull(event.getClickedBlock()).isPassable() &&
                    event.getClickedBlock().getLocation().getBlockX() == event.getPlayer().getLocation().getBlockX() &&
                    event.getClickedBlock().getLocation().getBlockZ() == event.getPlayer().getLocation().getBlockZ() &&
                    event.getClickedBlock().equals(event.getPlayer().getLocation().clone().add(0, 1.9, 0).getBlock())) {

                CrPlayer crPlayer = Crawling.getInstance().playerManager.getCrPlayer(event.getPlayer());
                if(crPlayer.isCrawling()) {
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTaskLater(Crawling.getInstance(), () -> crPlayer.addBarrierAbove(event.getClickedBlock()), 1);
                }
            }
        }
    }

}
