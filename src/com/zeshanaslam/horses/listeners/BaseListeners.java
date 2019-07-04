package com.zeshanaslam.horses.listeners;

import com.zeshanaslam.horses.Main;
import com.zeshanaslam.horses.config.ConfigStore;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.SafeLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
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

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (main.configStore.playerHorses.containsKey(entity.getUniqueId())) {
            PlayerHorse playerHorse = main.configStore.playerHorses.get(entity.getUniqueId());
            if (playerHorse.owner == null) {
                return;
            }

            if (!(playerHorse.owner.equals(player.getUniqueId()) || playerHorse.trusted.contains(player.getUniqueId()))) {
                event.setCancelled(true);
                player.sendMessage(main.configStore.getMessage(ConfigStore.Messages.NotTrusted));
            }
        }
    }
}
