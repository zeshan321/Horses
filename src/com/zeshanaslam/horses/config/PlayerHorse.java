package com.zeshanaslam.horses.config;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerHorse {

    public String showName;
    public String name;
    public int age;
    public String breed;
    public String disciplines;
    public String gender;
    public UUID owner;
    public UUID entity;
    public SafeLocation location;
    public List<UUID> trusted;

    public PlayerHorse() {
        age = -1;
        trusted = new ArrayList<>();
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setDisciplines(String disciplines) {
        this.disciplines = disciplines;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOwner(UUID owner) { this.owner = owner; }

    public void setLocation(SafeLocation location) {
        this.location = location;
    }

    public void addTrusted(UUID uuid) {
        trusted.add(uuid);
    }

    public void removeTrusted(UUID uuid) {
        trusted.remove(uuid);
    }
}
