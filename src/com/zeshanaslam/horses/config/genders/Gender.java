package com.zeshanaslam.horses.config.genders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Gender {
    public String type;
    public Material item;
    public String display;
    public List<String> lore;

    public Gender(String type, Material item, String display, List<String> lore) {
        this.type = type;
        this.item = item;
        this.display = display;
        this.lore = lore;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(display);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
