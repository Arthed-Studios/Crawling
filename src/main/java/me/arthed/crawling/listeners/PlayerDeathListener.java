package me.arthed.crawling.listeners;

import me.arthed.crawling.Crawling;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(Crawling.getInstance().isCrawling(event.getEntity()))
            Crawling.getInstance().stopCrawling(event.getEntity());
    }

}
