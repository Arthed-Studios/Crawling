package me.arthed.crawling.listeners;

import me.arthed.crawling.Crawling;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerDamageListener implements Listener {

    private final Crawling crawling = Crawling.getInstance();

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if(crawling.isCrawling(player)) {
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        }
    }

}
