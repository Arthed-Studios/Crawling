package me.arthed.crawling.listeners;

import me.arthed.crawling.Crawling;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;

public class SwimmingToggleListener implements Listener {

    private final Crawling crawling = Crawling.getInstance();

    @EventHandler
    public void onEntityToggleSwim(EntityToggleSwimEvent e) {
        if (!e.isSwimming() && e.getEntityType().equals(EntityType.PLAYER))
            if (crawling.isCrawling((Player) e.getEntity())) {
                e.setCancelled(true);
            }
    }

}
