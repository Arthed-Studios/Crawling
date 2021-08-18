package me.arthed.crawling.commands;

import me.arthed.crawling.Crawling;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CrawlingCommand implements CommandExecutor, TabExecutor {

    private final Crawling plugin;

    public CrawlingCommand(Crawling plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("crawling")) {
            if(!sender.hasPermission("crawl.admin") && !sender.hasPermission("crawling.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.plugin.config.getString("command_no_permission_message"))));
                return false;
            }
            if(args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                this.plugin.saveDefaultConfig();
                this.plugin.reloadConfig();
                this.plugin.config.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bConfig successfully reloaded!"));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&lCrawling &r&9made by Arthed"));
            sender.sendMessage("");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Use &r&9&l/crawl reload &r&9to reload the config!"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-"));
            return true;
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            if("reload".startsWith(args[0].toLowerCase())) {
                return Collections.singletonList("reload");
            }
        }
        return null;
    }
}
