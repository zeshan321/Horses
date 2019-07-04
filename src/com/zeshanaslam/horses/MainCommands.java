package com.zeshanaslam.horses;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.zeshanaslam.horses.config.ConfigStore;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.SafeLocation;
import com.zeshanaslam.horses.conversation.Conversation;
import com.zeshanaslam.horses.conversation.ConversationCallback;
import com.zeshanaslam.horses.conversation.ConversationObject;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class MainCommands extends BaseCommand {

    private Main plugin;
    public HashMap<UUID, Integer> following;

    public MainCommands(Main plugin) {
        super("h");
        this.plugin = plugin;
        this.following = new HashMap<>();
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help){
        help.showHelp();
    }

    @Subcommand("create")
    @CommandPermission("horses.create")
    @Description("Creates horse!")
    public void onCreate(Player player) {
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.EnterShowName));

        plugin.conversation.startConversation(new ConversationObject(plugin, player.getUniqueId(), Conversation.ConvoType.STRING, new ConversationCallback() {
            @Override
            public void onValid(Player player, String value) {
                switch (plugin.conversation.getStage(player)) {
                    case 0:
                        if (!plugin.configStore.isShowNameInUse(value)) {
                            PlayerHorse playerHorse = new PlayerHorse();
                            playerHorse.setShowName(value);

                            plugin.inventoryHelpers.creatingHorse.put(player.getUniqueId(), playerHorse);
                            plugin.conversation.newStage(player);
                            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.EnterName));
                        } else {
                            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.ShowNameUsed));
                        }
                        break;

                    case 1:
                        plugin.inventoryHelpers.creatingHorse.get(player.getUniqueId()).setName(value);
                        plugin.conversation.changeType(player, Conversation.ConvoType.INT);
                        plugin.conversation.newStage(player);
                        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.EnterAge));
                        break;

                    case 2:
                        int age = Integer.valueOf(value);
                        plugin.inventoryHelpers.creatingHorse.get(player.getUniqueId()).setAge(age);
                        plugin.conversation.endConversation(player);
                        plugin.inventoryHelpers.openBreedInv(player);
                        break;
                }
            }

            @Override
            public void onInvalid(Player player, String value) {
                player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.InvalidInput));
            }

            @Override
            public void onTimeout(Player player) {
                player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.CreateTimeout));
                plugin.inventoryHelpers.creatingHorse.remove(player.getUniqueId());
            }

            @Override
            public void onForceEnd(UUID playerUUID) {
                plugin.inventoryHelpers.creatingHorse.remove(playerUUID);
            }
        }));
    }

    @Subcommand("claim")
    @CommandPermission("horses.claim")
    @Description("Claims horse!")
    public void onClaim(Player player) {
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractHorse)) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.MustBeOnHorse));
            return;
        }

        PlayerHorse previous = plugin.configStore.playerHorses.get(vehicle.getUniqueId());
        if (previous == null) {
            player.sendMessage(ChatColor.RED + "Unable to claim wild horse!");
            return;
        }

        if (previous.owner != null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.AlreadyClaimed));
            return;
        }

        AbstractHorse abstractHorse = (AbstractHorse) vehicle;
        if (!abstractHorse.isTamed())
            return;

        PlayerHorse playerHorse = (previous == null) ? new PlayerHorse() : previous;
        playerHorse.location = new SafeLocation().fromLocation(vehicle.getLocation());
        playerHorse.entity = vehicle.getUniqueId();
        playerHorse.owner = player.getUniqueId();
        playerHorse.age = abstractHorse.getAge();
        playerHorse.name = abstractHorse.getCustomName();
        plugin.configStore.playerHorses.put(playerHorse.entity, playerHorse);

        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.ClaimedHorse));
    }

    @Subcommand("unclaim")
    @CommandPermission("horses.unclaim")
    @Description("Unclaims horse!")
    public void onUnclaim(Player player) {
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractHorse)) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.MustBeOnHorse));
            return;
        }

        if (!plugin.configStore.playerHorses.containsKey(vehicle.getUniqueId())) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotClaimed));
            return;
        }

        PlayerHorse playerHorse = plugin.configStore.playerHorses.get(vehicle.getUniqueId());
        if (!playerHorse.owner.equals(player.getUniqueId())) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.NotYourHorse));
            return;
        }

        // Clear following
        if (following.containsKey(playerHorse.entity)) {
            Bukkit.getScheduler().cancelTask(following.get(playerHorse.entity));
        }

        playerHorse.setOwner(null);
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.UnclaimHorse));
    }

    @Subcommand("list")
    @CommandPermission("horses.list")
    @Description("Horse list!")
    public void onHorse(Player player) {
        plugin.inventoryHelpers.openHorseList(player);
    }

    @Subcommand("tp")
    @CommandPermission("horses.tp")
    @Description("Teleports to horse!")
    @Syntax("<showname>")
    @CommandCompletion("@playerhorse")
    public void onTp(Player player, PlayerHorse playerHorse) {
        Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
        if (entity == null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
            return;
        }

        player.teleport(entity);
    }

    @Subcommand("tphere")
    @CommandPermission("horses.tphere")
    @Description("Teleports hotse to you!")
    @Syntax("<showname>")
    @CommandCompletion("@playerhorse")
    public void onTpHere(Player player, PlayerHorse playerHorse) {
        Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
        if (entity == null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
            return;
        }

        entity.teleport(player);
    }

    @Subcommand("kill")
    @CommandPermission("horses.kill")
    @Description("Kills horse!")
    @Syntax("<showname>")
    @CommandCompletion("@playerhorse")
    public void onKill(Player player, PlayerHorse playerHorse) {
        Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
        if (entity == null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.setHealth(0);
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.KilledHorse));
    }

    @Subcommand("trust")
    @CommandPermission("horses.trust")
    @Description("Trusts player to ride horse!")
    @Syntax("<player> <showname>")
    @CommandCompletion("@players @playerhorse")
    public void onTrust(Player player, OnlinePlayer onlinePlayer, PlayerHorse playerHorse) {
        UUID uuid = onlinePlayer.getPlayer().getUniqueId();
        if (playerHorse.owner.equals(uuid)) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.CannotAddSelf));
            return;
        }

        playerHorse.addTrusted(uuid);
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.AddedToTrsuted));
    }

    @Subcommand("untrust")
    @CommandPermission("horses.untrust")
    @Description("Untrusts player to ride horse!")
    @Syntax("<player> <showname>")
    @CommandCompletion("@players @playerhorse")
    public void onUntrust(Player player, OnlinePlayer onlinePlayer, PlayerHorse playerHorse) {
        UUID uuid = onlinePlayer.getPlayer().getUniqueId();
        if (!playerHorse.trusted.contains(uuid)) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.NotOnTrusted));
            return;
        }

        playerHorse.removeTrusted(uuid);
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.RemovedFromTrusted));
    }

    @Subcommand("follow")
    @CommandPermission("horses.follow")
    @Description("Makes horse follow you!")
    @Syntax("<showname>")
    @CommandCompletion("@playerhorse")
    public void onFollow(Player player, PlayerHorse playerHorse) {
        Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
        if (entity == null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        double speed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location location = player.getLocation();
            int distance = plugin.configStore.distanceTeleport;
            if (distance != -1) {
                if (entity.getLocation().distance(location) >= distance)
                    entity.teleport(player);
            }
            ((EntityInsentient) ((CraftEntity) livingEntity).getHandle()).getNavigation().a(location.getX(), location.getY(), location.getZ(), speed);
        }, 0, 20);

        if (following.containsKey(entity.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(following.remove(entity.getUniqueId()));
        }

        following.put(entity.getUniqueId(), bukkitTask.getTaskId());
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseFollowing));
    }

    @Subcommand("unfollow")
    @CommandPermission("horses.unfollow")
    @Description("Makes horse unfollow you!")
    @Syntax("<showname>")
    @CommandCompletion("@playerhorse")
    public void onUnfollow(Player player, PlayerHorse playerHorse) {
        Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
        if (entity == null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
            return;
        }

        if (!following.containsKey(entity.getUniqueId())) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.NotFollowingAlready));
            return;
        }

        Bukkit.getScheduler().cancelTask(following.remove(entity.getUniqueId()));
        player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseUnfollow));
    }
}
