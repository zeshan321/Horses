package com.zeshanaslam.horses.conversation;

import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ConversationCallback {

    public abstract void onValid(Player player, String value);

    public abstract void onInvalid(Player player, String value);

    public abstract void onTimeout(Player player);

    public abstract void onForceEnd(UUID playerUUID);

    /*public Conversation instance() {
        return Main.instance.conversation;
    }*/
}
