package me.arthed.crawling.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.arthed.crawling.Crawling;
import me.arthed.crawling.players.PlayerManager;

public class PlayerMoveListener implements Listener {

    private final PlayerManager playerManager;

    public PlayerMoveListener() {
        this.playerManager = Crawling.getInstance().playerManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Crawling.getInstance(), () -> playerManager.getCrPlayer(event.getPlayer()).onMove(event));
    }
}
