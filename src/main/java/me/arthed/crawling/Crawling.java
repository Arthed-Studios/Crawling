package me.arthed.crawling;

import me.arthed.crawling.commands.CrawlingCommand;
import me.arthed.crawling.utils.UpdateManager;
import me.arthed.crawling.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.arthed.crawling.config.CrawlingConfig;
import me.arthed.crawling.listeners.PlayerInteractListener;
import me.arthed.crawling.listeners.PlayerMoveListener;
import me.arthed.crawling.listeners.SneakingListener;
import me.arthed.crawling.listeners.SwimmingToggleListener;
import me.arthed.crawling.players.PlayerManager;
import me.arthed.crawling.utils.MetricsLite;

import java.util.Objects;

public class Crawling extends JavaPlugin implements Listener {

    static Crawling plugin;
    public static Crawling getInstance() {
        return plugin;
    }

    public WorldGuardUtils worldGuard;

    public CrawlingConfig config;

    public PlayerManager playerManager;

    @Override
    public void onEnable() {
        //Check if version is compatible
        if (!Bukkit.getVersion().contains("1.13") &&
                !Bukkit.getVersion().contains("1.14") &&
                !Bukkit.getVersion().contains("1.15") &&
                !Bukkit.getVersion().contains("1.16") &&
                !Bukkit.getVersion().contains("1.17")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry, this plugin works only on 1.13 or higher versions."));
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        //bstats
        new MetricsLite(this, 6915);

        plugin = this;
        config = new CrawlingConfig("config.yml");
        playerManager = new PlayerManager();

        getServer().getPluginManager().registerEvents(new SneakingListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new SwimmingToggleListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(this.getCommand("crawling")).setExecutor(new CrawlingCommand(this));

        saveDefaultConfig();

        if(!config.getBoolean("ignore_updates"))
            new UpdateManager(this).checkUpdates();
    }
    @Override
    public void onLoad() {
        Plugin worldGuardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuardPlugin != null) {
            //worldGuard = new WorldGuardUtils(worldGuardPlugin, this);
        }
    }

}
