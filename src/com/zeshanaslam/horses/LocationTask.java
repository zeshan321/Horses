package com.zeshanaslam.horses;

import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.SafeLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class LocationTask extends BukkitRunnable {

    private final Main main;

    public LocationTask(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        for (World world: Bukkit.getServer().getWorlds()) {
            for (Entity entity: world.getEntities()) {
                if (main.configStore.playerHorses.containsKey(entity.getUniqueId())) {
                    PlayerHorse playerHorse = main.configStore.playerHorses.get(entity.getUniqueId());
                    playerHorse.setLocation(new SafeLocation().fromLocation(entity.getLocation()));
                }
            }
        }
    }
}
