package com.zeshanaslam.horses.config.breeds;

import com.zeshanaslam.horses.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BreedsData {

    private final Main main;
    public HashMap<String, Breed> breeds;

    public BreedsData(Main main) {
        this.main = main;

        loadBreeds();
    }

    private void loadBreeds() {
        breeds = new HashMap<>();
        for (String key: main.getConfig().getConfigurationSection("Breeds").getKeys(false)) {
            Material item = Material.matchMaterial(main.getConfig().getString("Breeds." + key + ".Item"));
            String display = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Breeds." + key + ".Display"));
            List<String> lore = new ArrayList<>();
            for (String s: main.getConfig().getStringList("Breeds." + key + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            Horse.Variant variant = Horse.Variant.valueOf(main.getConfig().getString("Breeds." + key + ".Variant"));
            Horse.Style style = Horse.Style.valueOf(main.getConfig().getString("Breeds." + key + ".Style"));
            Horse.Color color = Horse.Color.valueOf(main.getConfig().getString("Breeds." + key + ".Color"));
            double jumpStrength = main.getConfig().getDouble("Breeds." + key + ".JumpStrength");
            double movementSpeed = main.getConfig().getDouble("Breeds." + key + ".MovementSpeed");
            double maxHealth = main.getConfig().getDouble("Breeds." + key + ".MaxHealth");

            breeds.put(key, new Breed(key, item, display, lore, variant, style, color, jumpStrength, movementSpeed, maxHealth));
        }

        System.out.println("Loaded " + breeds.size() + " breeds!");
    }
}
