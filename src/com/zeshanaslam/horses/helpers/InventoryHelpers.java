package com.zeshanaslam.horses.helpers;

import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.SafeLocation;
import com.zeshanaslam.horses.config.breeds.Breed;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.disciplines.Disciplines;
import com.zeshanaslam.horses.config.genders.Gender;
import me.libraryaddict.inventory.PageInventory;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InventoryHelpers {

    private final Main main;
    public HashMap<UUID, PlayerHorse> creatingHorse;

    public InventoryHelpers(Main main) {
        this.main = main;
        this.creatingHorse = new HashMap<>();
    }

    public void openBreedInv(Player player) {
        PageInventory inv = new PageInventory(player);

        inv.setPages(main.configStore.breedsData.breeds.values().stream().map(Breed::createItemStack).toArray(ItemStack[]::new));
        inv.setTitle(main.configStore.breedInvTitle);
        inv.setPageDisplayedInTitle(true);

        inv.openInventory();
    }

    public void openDisciplinesInv(Player player) {
        PageInventory inv = new PageInventory(player);

        inv.setPages(main.configStore.disciplinesData.disciplines.values().stream().map(Disciplines::createItemStack).toArray(ItemStack[]::new));
        inv.setTitle(main.configStore.disciplinesInvTitle);
        inv.setPageDisplayedInTitle(true);

        inv.openInventory();
    }

    public void openGenderInv(Player player) {
        PageInventory inv = new PageInventory(player);

        inv.setPages(main.configStore.gendersData.genders.values().stream().map(Gender::createItemStack).toArray(ItemStack[]::new));
        inv.setTitle(main.configStore.gendersInvTitle);
        inv.setPageDisplayedInTitle(true);

        inv.openInventory();
    }

    public void completeCreating(Player player) {
        // Get /h create data and remove from creating process
        PlayerHorse playerHorse = main.inventoryHelpers.creatingHorse.get(player.getUniqueId());
        main.inventoryHelpers.creatingHorse.remove(player.getUniqueId());
        player.closeInventory();

        // Get selected breed data and create entity
        Breed breed = main.configStore.breedsData.breeds.get(playerHorse.breed);
        Entity entity = breed.createEntity(player, playerHorse, player.getLocation());
        playerHorse.owner = player.getUniqueId();
        playerHorse.entity = entity.getUniqueId();
        playerHorse.location = new SafeLocation().fromLocation(entity.getLocation());

        // Store updated player horse data to save
        main.configStore.playerHorses.put(playerHorse.entity, playerHorse);
    }

    public void openHorseList(Player player) {
        PageInventory inv = new PageInventory(player);

        inv.setPages(main.configStore.playerHorses.values().stream().filter(playerHorse -> playerHorse.owner.equals(player.getUniqueId())).map(this::createPlayerHorseItemStack).toArray(ItemStack[]::new));
        inv.setTitle(main.configStore.horseInvTitle);
        inv.setPageDisplayedInTitle(true);

        inv.openInventory();
    }

    private ItemStack createPlayerHorseItemStack(PlayerHorse playerHorse) {
        ItemStack itemStack = new ItemStack(main.configStore.horseMaterial);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(replacePlaceholder(main.configStore.horseDisplay, playerHorse));

        itemMeta.setLore(main.configStore.horseLore.stream().map(s -> replacePlaceholder(s, playerHorse)).collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private String replacePlaceholder(String s, PlayerHorse playerHorse) {
        return s.replace("%name%", (playerHorse.name == null) ? "Not set" : playerHorse.name)
                .replace("%age%", (playerHorse.age == -1) ? "Not set" : playerHorse.age + "")
                .replace("%breed%", (playerHorse.breed == null) ? "Not set" : playerHorse.breed)
                .replace("%disciplines%", (playerHorse.disciplines == null) ? "Not set" : playerHorse.disciplines)
                .replace("%gender%", (playerHorse.gender == null) ? "Not set" : playerHorse.gender)
                .replace("%showname%", (playerHorse.gender == null) ? "Not set" : playerHorse.showName);
    }

    public Entity getEntity(UUID uuid) {
        PlayerHorse playerHorse = main.configStore.playerHorses.get(uuid);
        if (playerHorse.location != null) {
            Location location = playerHorse.location.getLocation();
            location.getChunk().load(true);

            for (Entity entity: location.getChunk().getEntities()) {
                if (entity.getUniqueId().equals(uuid)) {
                    return entity;
                }
            }
        }
        return null;
    }
}
