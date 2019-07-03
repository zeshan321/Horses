package com.zeshanaslam.horses.conversation;

import com.zeshanaslam.horses.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class ConversationObject {

    public UUID playerUUID;
    public Conversation.ConvoType type;
    public ConversationCallback callback;
    public int taskID;
    public int stage = 0;

    public long timeoutTicks = 300;

    public ConversationObject(Main main, UUID playerUUID, Conversation.ConvoType type, ConversationCallback callback) {
        this.playerUUID = playerUUID;
        this.type = type;
        this.callback = callback;

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    main.conversation.endConversation(player);
                    callback.onTimeout(player);
                }
            }
        }.runTaskLater(main, timeoutTicks);

        taskID = task.getTaskId();
    }
}
