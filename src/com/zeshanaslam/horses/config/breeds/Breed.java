package com.zeshanaslam.horses.config.breeds;

import com.zeshanaslam.horses.config.PlayerHorse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Breed {
    public String type;
    public Material item;
    public String display;
    public List<String> lore;
    public Horse.Variant variant;
    public Horse.Style style;
    public Horse.Color color;
    public double jumpStrength;
    public double movementSpeed;
    public double maxHealth;

    public Breed(String type, Material item, String display, List<String> lore, Horse.Variant variant, Horse.Style style, Horse.Color color, double jumpStrength, double movementSpeed, double maxHealth) {
        this.type = type;
        this.item = item;
        this.display = display;
        this.lore = lore;
        this.variant = variant;
        this.style = style;
        this.color = color;
        this.jumpStrength = jumpStrength;
        this.movementSpeed = movementSpeed;
        this.maxHealth = maxHealth;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(display);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Entity createEntity(Player player, PlayerHorse playerHorse, Location location) {
        AbstractHorse entity = null;
        switch (variant) {
            case DONKEY:
                entity = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.DONKEY);
                break;
            case HORSE:
                Horse horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
                horse.setColor(color);
                horse.setStyle(style);
                entity = horse;
                break;
            case LLAMA:
                entity = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.LLAMA);
                break;
            case MULE:
                entity = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.MULE);
                break;
            case SKELETON_HORSE:
                entity = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.SKELETON_HORSE);
                break;
            case UNDEAD_HORSE:
                entity = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.ZOMBIE_HORSE);
                break;
        }

        entity.setDomestication(entity.getMaxDomestication());
        entity.setAge(playerHorse.age);
        entity.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        entity.setJumpStrength(jumpStrength);
        entity.setTamed(true);
        entity.setOwner(player);
        entity.setCustomName(playerHorse.name);
        entity.setCustomNameVisible(true);
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        entity.setPassenger(player);
        return entity;
    }
}
