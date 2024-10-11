package dev.xdpxi.xdlib.plugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

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
            player.sendMessage(ChatColor.RED + "No teleport request found.");
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        requester.teleport(player.getLocation());
        player.sendMessage(ChatColor.GOLD + "Teleporting...");
        requester.sendMessage(ChatColor.GOLD + "Teleport request " + ChatColor.GREEN + "accepted" + ChatColor.GOLD + " by " + ChatColor.GREEN + player.getName() + ChatColor.GOLD + ".");
    }

    private void handleDecline(Player player) {
        UUID requesterUUID = teleportRequests.remove(player.getUniqueId());
        if (requesterUUID == null) {
            player.sendMessage(ChatColor.RED + "No teleport request found.");
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Teleport request " + ChatColor.RED + "declined" + ChatColor.GOLD + ".");
        requester.sendMessage(ChatColor.GOLD + "Teleport request" + ChatColor.RED + " declined" + ChatColor.GOLD + " by " + ChatColor.GREEN + player.getName());
    }

    private void handleTpaRequest(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        teleportRequests.put(target.getUniqueId(), player.getUniqueId());

        TextComponent message = new TextComponent(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " has requested to teleport to you. \n");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "[Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept"));
        TextComponent decline = new TextComponent(ChatColor.RED + "[Decline]");
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa decline"));

        message.addExtra(accept);
        message.addExtra(" ");
        message.addExtra(decline);

        target.spigot().sendMessage(message);
        player.sendMessage(ChatColor.GOLD + "Teleport request sent to " + ChatColor.GREEN + target.getName() + ChatColor.GOLD + ".");
    }
}