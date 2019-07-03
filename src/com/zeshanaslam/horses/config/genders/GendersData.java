package com.zeshanaslam.horses.config.genders;

import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.disciplines.Disciplines;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GendersData {

    private final Main main;
    public HashMap<String, Gender> genders;

    public GendersData(Main main) {
        this.main = main;

        loadGenders();
    }

    private void loadGenders() {
        genders = new HashMap<>();
        for (String key: main.getConfig().getConfigurationSection("Genders").getKeys(false)) {
            Material item = Material.matchMaterial(main.getConfig().getString("Genders." + key + ".Item"));
            String display = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Genders." + key + ".Display"));
            List<String> lore = new ArrayList<>();
            for (String s: main.getConfig().getStringList("Genders." + key + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            genders.put(key, new Gender(key, item, display, lore));
        }

        System.out.println("Loaded " + genders.size() + " genders!");
    }
}
