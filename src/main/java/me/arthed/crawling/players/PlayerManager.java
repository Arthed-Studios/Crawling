package me.arthed.crawling.players;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.arthed.crawling.Crawling;

public class PlayerManager implements Listener {

    private final HashMap<Player, CrPlayer> players = new HashMap<>();

    public PlayerManager() {
        Bukkit.getPluginManager().registerEvents(this, Crawling.getInstance());
    }

    public CrPlayer getCrPlayer(Player player) {
        if(!players.containsKey(player))
            players.put(player, new CrPlayer(player));
        return players.get(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        players.put(event.getPlayer(), new CrPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        players.get(event.getPlayer()).stopCrawling();
        players.remove(event.getPlayer());
    }


}
