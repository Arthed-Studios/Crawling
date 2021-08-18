package me.arthed.crawling.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleSwimEvent;

import me.arthed.crawling.Crawling;
import me.arthed.crawling.players.PlayerManager;

import org.bukkit.event.Listener;

public class SwimmingToggleListener implements Listener {

    PlayerManager playerManager;

    public SwimmingToggleListener() {
        playerManager = Crawling.getInstance().playerManager;
    }

    @EventHandler
    public void onEntityToggleSwim(EntityToggleSwimEvent e) {
        if(e.getEntityType().equals(EntityType.PLAYER))
            if(playerManager.getCrPlayer((Player) e.getEntity()).isCrawling() && !e.isSwimming())
                e.setCancelled(true);
    }

}
