package dev.xdpxi.xdlib.plugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class joinLeaveListener implements Listener {
    private final xdlib plugin;

    public joinLeaveListener(xdlib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GREEN + ">>> " + ChatColor.WHITE + event.getPlayer().getName());
        if (!event.getPlayer().hasPlayedBefore() && plugin.getConfig().getBoolean("welcomeMessage")) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome " + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.GOLD + " to the server!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.RED + "<<< " + ChatColor.WHITE + event.getPlayer().getName());
    }
}