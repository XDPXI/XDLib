package dev.xdpxi.xdlib.plugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class tpaCommand implements CommandExecutor {
    private final HashMap<UUID, UUID> teleportRequests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /tpa <player> | /tpa accept | /tpa decline");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            handleAccept(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("decline")) {
            handleDecline(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("ask")) {
            handleTpaRequest(player, args[1]);
            return true;
        }

        return false;
    }

    private void handleAccept(Player player) {
        UUID requesterUUID = teleportRequests.remove(player.getUniqueId());
        if (requesterUUID == null) {
            player.sendMessage(Component.text("No teleport request found.", NamedTextColor.RED));
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null) {
            player.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }

        requester.teleport(player.getLocation());
        player.sendMessage(Component.text("Teleporting...", NamedTextColor.GOLD));
        requester.sendMessage(Component.text("Teleport request accepted by " + player.getName(), NamedTextColor.GOLD));
    }

    private void handleDecline(Player player) {
        UUID requesterUUID = teleportRequests.remove(player.getUniqueId());
        if (requesterUUID == null) {
            player.sendMessage(Component.text("No teleport request found.", NamedTextColor.RED));
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null) {
            player.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Teleport request declined.", NamedTextColor.GOLD));
        requester.sendMessage(Component.text("Teleport request declined by " + player.getName(), NamedTextColor.GOLD));
    }

    private void handleTpaRequest(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }

        teleportRequests.put(target.getUniqueId(), player.getUniqueId());

        TextComponent message = new TextComponent(player.getName() + " has requested to teleport to you. \n");
        TextComponent accept = new TextComponent("[Accept]");
        accept.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept"));
        TextComponent decline = new TextComponent("[Decline]");
        decline.setColor(net.md_5.bungee.api.ChatColor.RED);
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa decline"));

        message.addExtra(accept);
        message.addExtra(" ");
        message.addExtra(decline);

        target.spigot().sendMessage(message);
        player.sendMessage(Component.text("Teleport request sent to " + target.getName(), NamedTextColor.GOLD));
    }
}