package me.arthed.crawling;

import me.arthed.crawling.commands.CrawlingCommand;
import me.arthed.crawling.impl.WorldGuardImplementation;
import me.arthed.crawling.listeners.PlayerDeathListener;
import me.arthed.crawling.listeners.PlayerInteractListener;
import me.arthed.crawling.listeners.SneakingListener;
import me.arthed.crawling.listeners.SwimmingToggleListener;
import me.arthed.crawling.nms.v1_15.NmsPackets_v1_15;
import me.arthed.crawling.nms.v1_16.NmsPackets_v1_16;
import me.arthed.crawling.nms.v1_17.NmsPackets_v1_17;
import me.arthed.crawling.nms.v1_18.NmsPackets_v1_18;
import me.arthed.crawling.utils.BlockUtils;
import me.arthed.crawling.utils.MetricsLite;
import me.arthed.crawling.utils.UpdateManager;
import me.arthed.nms.NmsPackets;
import me.arthed.nms.v1_14.NmsPackets_v1_14;
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

    private NmsPackets nmsPacketManager;
    public NmsPackets getNmsPacketManager() {
        return nmsPacketManager;
    }

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
        //Check if version is compatible and set the proper nms packet manager
        if(Bukkit.getVersion().contains("1.14"))
            this.nmsPacketManager = new NmsPackets_v1_14();
        else if(Bukkit.getVersion().contains("1.15"))
            this.nmsPacketManager = new NmsPackets_v1_15();
        else if(Bukkit.getVersion().contains("1.16"))
            this.nmsPacketManager = new NmsPackets_v1_16();
        else if(Bukkit.getVersion().contains("1.17"))
            this.nmsPacketManager = new NmsPackets_v1_17();
        else if(Bukkit.getVersion().contains("1.18"))
            this.nmsPacketManager = new NmsPackets_v1_18();
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry, this plugin works only on 1.14 or higher versions."));
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        //bstats
        new MetricsLite(this, 6915);

        plugin = this;
        config = new CrawlingConfig("config.yml");

        getServer().getPluginManager().registerEvents(new SneakingListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new SwimmingToggleListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        Objects.requireNonNull(this.getCommand("crawling")).setExecutor(new CrawlingCommand(this));
        BlockUtils.setup();

        saveDefaultConfig();

        if(!config.getBoolean("ignore_updates"))
            new UpdateManager(this).checkUpdates();
    }

    @Override
    public void onLoad() {
        Plugin worldGuardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if(worldGuardPlugin != null) {
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
