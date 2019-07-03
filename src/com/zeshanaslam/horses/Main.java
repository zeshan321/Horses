package com.zeshanaslam.horses;

import co.aikar.commands.PaperCommandManager;
import com.zeshanaslam.horses.config.ConfigStore;
import com.zeshanaslam.horses.conversation.Conversation;
import com.zeshanaslam.horses.conversation.ConversationEvents;
import com.zeshanaslam.horses.helpers.InventoryHelpers;
import com.zeshanaslam.horses.listeners.BaseListeners;
import com.zeshanaslam.horses.listeners.InventoryListeners;
import org.bukkit.plugin.java.JavaPlugin;

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
}
