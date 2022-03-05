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
    private final String currentVersion;


    public UpdateManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.currentVersion = plugin.getDescription().getVersion();
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
                if (br.readLine().equals(currentVersion)) {
                    update = Boolean.FALSE;
                }
                else {
                    update = Boolean.TRUE;
                    if (currentVersion.equals("5.2.2-SNAPSHOT")){
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThis is snapshot version! Check out the updates often: https://www.spigotmc.org/resources/69126/"));
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            if(p.isOp()) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThis is snapshot version! Check out the updates often: https://www.spigotmc.org/resources/69126/"));
                            }
                        }
                    }
                    else{
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThere is an update availabe! Download it from: https://www.spigotmc.org/resources/69126/"));
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            if(p.isOp()) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Crawling] &aThere is an update availabe! Download it from: https://www.spigotmc.org/resources/69126/"));
                            }
                        }
                    }

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
