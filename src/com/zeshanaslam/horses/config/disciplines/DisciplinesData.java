package com.zeshanaslam.horses.config.disciplines;

import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.breeds.Breed;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisciplinesData {

    private final Main main;
    public HashMap<String, Disciplines> disciplines;

    public DisciplinesData(Main main) {
        this.main = main;

        loadDisciplines();
    }

    private void loadDisciplines() {
        disciplines = new HashMap<>();
        for (String key: main.getConfig().getConfigurationSection("Disciplines").getKeys(false)) {
            Material item = Material.matchMaterial(main.getConfig().getString("Disciplines." + key + ".Item"));
            String display = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Disciplines." + key + ".Display"));
            List<String> lore = new ArrayList<>();
            for (String s: main.getConfig().getStringList("Disciplines." + key + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            disciplines.put(key, new Disciplines(key, item, display, lore));
        }

        System.out.println("Loaded " + disciplines.size() + " disciplines!");
    }
}
