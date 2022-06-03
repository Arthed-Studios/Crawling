package me.arthed.crawling.config;

import me.arthed.crawling.Crawling;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CrawlingConfig extends YamlConfiguration {

    private final File configFile;
    private Map<String, Object> data;

    public CrawlingConfig(String fileName) {
        super();

        Crawling plugin = Crawling.getInstance();
        configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }
        reload();

        InputStream defaultConfigInputStream = Crawling.class.getResourceAsStream("/" + fileName);
        InputStreamReader defaultConfigReader = new InputStreamReader(defaultConfigInputStream);
        setDefaults(YamlConfiguration.loadConfiguration(defaultConfigReader));

    }

    public void reload() {
        try {
            load(configFile);
            data = new HashMap<>();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Set<Material> getMaterialList(String path) {
        if (data.containsKey(path))
            return (Set<Material>) data.get(path);

        Set<Material> result = new HashSet<>();
        for (String materialName : getStringList(path)) {
            try {
                result.add(Material.valueOf(materialName));
            } catch (IllegalArgumentException ignore) {
            }
        }
        data.put(path, result);
        return result;
    }

    public Set<World> getWorldList(String path) {
        if (data.containsKey(path))
            return (Set<World>) data.get(path);

        Set<World> result = new HashSet<>();
        for (String worldName : getStringList(path)) {
            World world = Bukkit.getWorld(worldName);
            if (world != null)
                result.add(world);
        }
        data.put(path, result);
        return result;
    }

}
