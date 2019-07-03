package com.zeshanaslam.horses;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.zeshanaslam.horses.config.ConfigStore;
import com.zeshanaslam.horses.config.PlayerHorse;
import com.zeshanaslam.horses.config.SafeLocation;
import com.zeshanaslam.horses.conversation.Conversation;
import com.zeshanaslam.horses.conversation.ConversationCallback;
import com.zeshanaslam.horses.conversation.ConversationObject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.Optional;
import java.util.UUID;

public class MainCommands extends BaseCommand {

    private Main plugin;

    public MainCommands(Main plugin) {
        super("h");
        this.plugin = plugin;
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
        if (previous != null && previous.owner != null) {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.AlreadyClaimed));
            return;
        }

        AbstractHorse abstractHorse = (AbstractHorse) vehicle;

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
    public void onTp(Player player, String name) {
        Optional<PlayerHorse> optionalPlayerHorse = plugin.configStore.playerHorses.values().stream().filter(p -> p.entity.toString().equals(name)).findFirst();
        if (optionalPlayerHorse.isPresent()) {
            PlayerHorse playerHorse = optionalPlayerHorse.get();
            Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
            if (entity == null) {
                player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
                return;
            }

            player.teleport(entity);
        } else {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
        }
    }

    @Subcommand("tphere")
    @CommandPermission("horses.tphere")
    @Description("Teleports hotse to you!")
    public void onTpHere(Player player, String name) {
        Optional<PlayerHorse> optionalPlayerHorse = plugin.configStore.playerHorses.values().stream().filter(p -> p.entity.toString().equals(name)).findFirst();
        if (optionalPlayerHorse.isPresent()) {
            PlayerHorse playerHorse = optionalPlayerHorse.get();
            Entity entity = plugin.inventoryHelpers.getEntity(playerHorse.entity);
            if (entity == null) {
                player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
                return;
            }

            entity.teleport(player);
        } else {
            player.sendMessage(plugin.configStore.getMessage(ConfigStore.Messages.HorseNotFound));
        }
    }
}
