package me.arthed.crawling.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager implements Listener {

    public boolean update;
    private final Version currentVersion;


    public UpdateManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        //Make sure plugin description version is always a valid version (i.e: 1.0, 4.1.2 etc), or errors will be thrown.
        this.currentVersion = new Version(plugin.getDescription().getVersion());
    }

    public void checkUpdates() {
        Thread thread = new Thread(() -> {
            URL url = null;
            try {
                url = new URL("https://api.spigotmc.org/legacy/update.php?resource=69126");
            } catch (MalformedURLException ignored) {}
            URLConnection conn = null;
            try {
                assert url != null;
                conn = url.openConnection();
            } catch (IOException ignored) {}
            try {
                assert conn != null;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //Checking if version is newer than the current.
                if (new Version(br.readLine()).compareTo(currentVersion) > 0) {
                    update = Boolean.TRUE;
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThere is an update availabe! Download it from: https://www.spigotmc.org/resources/69126/"));
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.isOp()) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThere is an update availabe! Download it from: https://www.spigotmc.org/resources/69126/"));
                        }
                    }
                } else {
                    update = Boolean.FALSE;
                }
            } catch (IOException ignored) {}
        });

        thread.start();

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p.isOp() && update) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThere is an update availabe! Download it from: https://www.spigotmc.org/resources/69126/"));
        }
    }

}
