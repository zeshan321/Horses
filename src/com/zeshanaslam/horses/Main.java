package com.zeshanaslam.horses;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.zeshanaslam.horses.config.ConfigStore;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.conversation.Conversation;
import com.zeshanaslam.horses.conversation.ConversationEvents;
import com.zeshanaslam.horses.helpers.InventoryHelpers;
import com.zeshanaslam.horses.listeners.BaseListeners;
import com.zeshanaslam.horses.listeners.InventoryListeners;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    private static PaperCommandManager commandManager;
    public ConfigStore configStore;
    public Conversation conversation;
    public InventoryHelpers inventoryHelpers;

    @Override
    public void onEnable() {
        super.onEnable();

        // Config
        saveDefaultConfig();
        configStore = new ConfigStore(this);

        // Conversion
        conversation = new Conversation();

        // Inventory helpers
        inventoryHelpers = new InventoryHelpers(this);

        // Commands
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        playerHorseComplete();
        commandManager.registerCommand(new MainCommands(this));

        // Listeners
        getServer().getPluginManager().registerEvents(new ConversationEvents(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        getServer().getPluginManager().registerEvents(new BaseListeners(this), this);

        // Location task
        new LocationTask(this).runTaskTimer(this,0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        commandManager.unregisterCommands();
        configStore.save();
    }

    private void playerHorseComplete() {
        commandManager.getCommandContexts().registerContext(PlayerHorse.class, context -> {
            UUID uuid = context.getPlayer().getUniqueId();
            Optional<PlayerHorse> optionalPlayerHorse = configStore.playerHorses.values().stream().filter(p -> p.owner.equals(uuid) && p.showName.equalsIgnoreCase(context.popLastArg())).findFirst();
            if (optionalPlayerHorse.isPresent()) {
                return optionalPlayerHorse.get();
            }

            throw new InvalidCommandArgument(configStore.getMessage(ConfigStore.Messages.HorseNotFound));
        });

        commandManager.getCommandCompletions().registerCompletion("playerhorse", context -> {
            Player player = context.getPlayer();
            return configStore.playerHorses.values().stream().filter(p -> p.owner.equals(player.getUniqueId())).map(p -> p.showName).collect(Collectors.toList());
        });
    }
}
