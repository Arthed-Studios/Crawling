package me.arthed.crawling.utils;

import me.arthed.crawling.Crawling;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockUtils {

    public static Set<Material> nonFullBlocks = new HashSet<>();
    public static Map<Material, Material> similarSoundBlocks = new HashMap<>();


    public static void setup() {
        Bukkit.getScheduler().runTaskAsynchronously(Crawling.getInstance(), () -> {
            BufferedReader brNonFullBlocks = new BufferedReader(new InputStreamReader(BlockUtils.class.getResourceAsStream("/nonFullBlocks")));
            BufferedReader brSimilarSounds = new BufferedReader(new InputStreamReader(BlockUtils.class.getResourceAsStream("/similarSoundBlocks")));
            try {
                for (String line = brNonFullBlocks.readLine(); line != null; line = brNonFullBlocks.readLine()) {
                    try {
                        Material material = Material.valueOf(line);
                        nonFullBlocks.add(material);
                        similarSoundBlocks.put(material, Material.BARRIER);
                    } catch(IllegalArgumentException ignore) {}
                }
                for (String line = brSimilarSounds.readLine(); line != null; line = brSimilarSounds.readLine()) {
                    try {
                        String[] materials = line.split(":");
                        similarSoundBlocks.put(Material.valueOf(materials[0]), Material.valueOf(materials[1]));
                    } catch(IllegalArgumentException ignore) {}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
