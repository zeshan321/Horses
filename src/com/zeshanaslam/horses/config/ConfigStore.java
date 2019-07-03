package com.zeshanaslam.horses.config;

import com.google.gson.Gson;
import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.breeds.BreedsData;
import com.zeshanaslam.horses.config.disciplines.DisciplinesData;
import com.zeshanaslam.horses.config.genders.GendersData;
import com.zeshanaslam.horses.utils.FileHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigStore {

    private final Main main;
    private final Gson gson;
    public BreedsData breedsData;
    public DisciplinesData disciplinesData;
    public GendersData gendersData;
    public HashMap<Messages, String> messages;
    public String breedInvTitle;
    public String disciplinesInvTitle;
    public String gendersInvTitle;
    public HashMap<UUID, PlayerHorse> playerHorses;
    public String horseDisplay;
    public List<String> horseLore;
    public Material horseMaterial;
    public String horseInvTitle;

    public ConfigStore(Main main) {
        this.gson = new Gson();
        this.main = main;

        // Load messages
        messages = new HashMap<>();
        for (String key: main.getConfig().getConfigurationSection("Messages").getKeys(false)) {
            messages.put(Messages.valueOf(key), ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Messages." + key)));
        }

        // Load breed data
        breedsData = new BreedsData(main);

        // Load disciplines data
        disciplinesData = new DisciplinesData(main);

        // Load gender data
        gendersData = new GendersData(main);

        // Inv titles
        breedInvTitle = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("BreedGUI.Title"));
        disciplinesInvTitle = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("DisciplinesGUI.Title"));
        gendersInvTitle = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("GendersGUI.Title"));
        horseInvTitle = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("HorseGUI.Title"));

        // Load player horses
        loadPlayerHorses();

        // Horse item display
        horseMaterial = Material.matchMaterial(main.getConfig().getString("HorseItem.Item"));
        horseDisplay = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("HorseItem.Display"));
        horseLore = new ArrayList<>();
        for (String s: main.getConfig().getStringList("HorseItem.Lore")) {
            horseLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public enum Messages {
        EnterShowName,
        EnterName,
        EnterAge,
        CreateTimeout,
        InvalidInput,
        ShowNameUsed,
        MustBeOnHorse,
        AlreadyClaimed,
        HorseNotClaimed,
        NotYourHorse,
        UnclaimHorse,
        ClaimedHorse,
        HorseNotFound
    }

    public String getMessage(Messages type) {
        return messages.get(type);
    }

    private void loadPlayerHorses() {
        playerHorses = new HashMap<>();
        FileHandler data = new FileHandler("plugins/Horses/data.yml");
        if (data.contains("Data")) {
            for (String stringGson: data.getStringList("Data")) {
                PlayerHorse playerHorse = gson.fromJson(stringGson, PlayerHorse.class);
                playerHorses.put(playerHorse.entity, playerHorse);
            }
        }

        System.out.println("Loaded " + playerHorses.size() + " saved horses!");
    }

    public void save() {
        FileHandler data = new FileHandler("plugins/Horses/data.yml");

        List<String> stringGson = new ArrayList<>();
        for (PlayerHorse playerHorse: playerHorses.values()) {
            stringGson.add(gson.toJson(playerHorse));
        }

        data.createNewStringList("Data", stringGson);
        data.save();

        System.out.println("Saved " + stringGson.size() + " horses!");
    }

    public boolean isShowNameInUse(String name) {
        for (PlayerHorse playerHorse: playerHorses.values()) {
            if (playerHorse.showName == null)
                continue;

            if (playerHorse.showName.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}
