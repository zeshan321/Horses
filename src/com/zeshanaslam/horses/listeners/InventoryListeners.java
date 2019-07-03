package com.zeshanaslam.horses.listeners;

import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.breeds.Breed;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryListeners implements Listener {

    private final Main main;

    public InventoryListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBreedClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (event.getInventory().getTitle() == null || !event.getInventory().getTitle().startsWith(main.configStore.breedInvTitle)){
            return;
        }

        ItemStack item = event.getCurrentItem();
        if ((item == null) || (item.getItemMeta() == null) || (item.getItemMeta().getDisplayName() == null)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String strippedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (main.configStore.breedsData.breeds.containsKey(strippedName)) {
            main.inventoryHelpers.creatingHorse.get(player.getUniqueId()).setBreed(strippedName);
            main.inventoryHelpers.openDisciplinesInv(player);
        }
    }

    @EventHandler
    public void onDisciplinesClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (event.getInventory().getTitle() == null || !event.getInventory().getTitle().startsWith(main.configStore.disciplinesInvTitle)){
            return;
        }

        ItemStack item = event.getCurrentItem();
        if ((item == null) || (item.getItemMeta() == null) || (item.getItemMeta().getDisplayName() == null)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String strippedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (main.configStore.disciplinesData.disciplines.containsKey(strippedName)) {
            main.inventoryHelpers.creatingHorse.get(player.getUniqueId()).setDisciplines(strippedName);
            main.inventoryHelpers.openGenderInv(player);
        }
    }

    @EventHandler
    public void onGenderClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (event.getInventory().getTitle() == null || !event.getInventory().getTitle().startsWith(main.configStore.gendersInvTitle)){
            return;
        }

        ItemStack item = event.getCurrentItem();
        if ((item == null) || (item.getItemMeta() == null) || (item.getItemMeta().getDisplayName() == null)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String strippedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (main.configStore.gendersData.genders.containsKey(strippedName)) {
            main.inventoryHelpers.creatingHorse.get(player.getUniqueId()).setGender(strippedName);
            main.inventoryHelpers.completeCreating(player);
        }
    }
}
