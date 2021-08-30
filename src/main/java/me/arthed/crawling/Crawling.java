package me.arthed.crawling;

import me.arthed.crawling.commands.CrawlingCommand;
import me.arthed.crawling.impl.WorldGuardImplementation;
import me.arthed.crawling.listeners.PlayerDamageListener;
import me.arthed.crawling.listeners.PlayerInteractListener;
import me.arthed.crawling.listeners.SneakingListener;
import me.arthed.crawling.listeners.SwimmingToggleListener;
import me.arthed.crawling.utils.MetricsLite;
import me.arthed.crawling.utils.UpdateManager;
import me.arthed.crawling.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.arthed.crawling.config.CrawlingConfig;

import java.util.HashMap;
import java.util.Objects;

public class Crawling extends JavaPlugin implements Listener {

    private static Crawling plugin;
    public static Crawling getInstance() {
        return plugin;
    }

    private final HashMap<Player, CrPlayer> playersCrawling = new HashMap<>();

    private WorldGuardImplementation worldGuard;
    public WorldGuardImplementation getWorldGuard() {
        return this.worldGuard;
    }

    private CrawlingConfig config;
    public CrawlingConfig getConfig() {
        return this.config;
    }

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

        getServer().getPluginManager().registerEvents(new SneakingListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
        getServer().getPluginManager().registerEvents(new SwimmingToggleListener(), this);

        Objects.requireNonNull(this.getCommand("crawling")).setExecutor(new CrawlingCommand(this));

        saveDefaultConfig();

        if(!config.getBoolean("ignore_updates"))
            new UpdateManager(this).checkUpdates();
    }

    @Override
    public void onLoad() {
        Plugin worldGuardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuardPlugin != null) {
            worldGuard = new WorldGuardImplementation(worldGuardPlugin, this);
        }
    }

    @Override
    public void onDisable() {
        for(Player playerCrawling : this.playersCrawling.keySet()) {
            Block blockAbovePlayer = playerCrawling.getLocation().add(0, 1.5, 0).getBlock();
            playerCrawling.sendBlockChange(blockAbovePlayer.getLocation(), blockAbovePlayer.getBlockData());
            blockAbovePlayer.getState().update();
        }
    }

    public void startCrawling(Player player) {
        if(!this.playersCrawling.containsKey(player)) {
            this.playersCrawling.put(player, new CrPlayer(player));
        }
    }

    public void stopCrawling(Player player) {
        this.playersCrawling.remove(player);
    }

    public boolean isCrawling(Player player) {
        return player.isSwimming() && this.playersCrawling.containsKey(player);
    }

    public CrPlayer getPlayerCrawling(Player player) {
        return this.playersCrawling.get(player);
    }



}
