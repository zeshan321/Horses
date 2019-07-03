package com.zeshanaslam.horses.listeners;

import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.SafeLocation;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class BaseListeners implements Listener {

    private final Main main;

    public BaseListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        main.configStore.playerHorses.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        for (Entity entity: event.getChunk().getEntities()) {
            if (main.configStore.playerHorses.containsKey(entity.getUniqueId())) {
                main.configStore.playerHorses.get(entity.getUniqueId()).setLocation(new SafeLocation().fromLocation(entity.getLocation()));
            }
        }
    }
}
