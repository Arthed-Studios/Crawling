package me.arthed.crawling;

import me.arthed.crawling.commands.CrawlingCommand;
import me.arthed.crawling.config.CrawlingConfig;
import me.arthed.crawling.impl.WorldGuardImplementation;
import me.arthed.crawling.listeners.PlayerDeathListener;
import me.arthed.crawling.listeners.PlayerInteractListener;
import me.arthed.crawling.listeners.SneakingListener;
import me.arthed.crawling.listeners.SwimmingToggleListener;
import me.arthed.crawling.nms.LegacyIndependentNmsPackets;
import me.arthed.crawling.nms.VersionIndependentNmsPackets;
import me.arthed.crawling.utils.BlockUtils;
import me.arthed.crawling.utils.MetricsLite;
import me.arthed.crawling.utils.UpdateManager;
import me.arthed.crawling.utils.Version;
import me.arthed.nms.NmsPackets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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

    private final static Version bukkitVersion = new Version(Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")));
    private final static Version maxSupportedVersion = new Version("1.18.2");
    private final static Version minSupportedVersion = new Version("1.14");

    private static boolean isLegacy() {
        try {
            // The minecraft server version was removed from the package name on newer versions.
            Class.forName("net.minecraft.server.MinecraftServer");
            return false;
        } catch (ClassNotFoundException e) {
            // Class was not found, therefore packages still have different names on each version.
            return true;
        }
    }

    @Override
    public void onEnable() {
        //Checking if version is lower than 1.14
        if (bukkitVersion.compareTo(minSupportedVersion) < 0) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry, this plugin works only on 1.14 or higher versions."));
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        //Checking which NmsPacketManager should be used.
        if (isLegacy()) {
            nmsPacketManager = new LegacyIndependentNmsPackets(Bukkit.getWorlds().get(0));
        } else {
            nmsPacketManager = new VersionIndependentNmsPackets(Bukkit.getWorlds().get(0));

            //Checking if current version was not tested yet
            if (bukkitVersion.compareTo(maxSupportedVersion) > 0) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe plugin was not made for this version, proceed with caution."));
            }
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

        if (!config.getBoolean("ignore_updates"))
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
        for (Player playerCrawling : this.playersCrawling.keySet()) {
            Block blockAbovePlayer = playerCrawling.getLocation().add(0, 1.5, 0).getBlock();
            playerCrawling.sendBlockChange(blockAbovePlayer.getLocation(), blockAbovePlayer.getBlockData());
            blockAbovePlayer.getState().update();
        }
    }

    public void startCrawling(Player player) {
        if (!this.playersCrawling.containsKey(player)) {
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
