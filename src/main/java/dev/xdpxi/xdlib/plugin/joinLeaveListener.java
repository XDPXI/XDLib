package dev.xdpxi.xdlib.plugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

        Player player = event.getPlayer();
        if (player.isOp()) {
            if (updateChecker.isUpdate()) {
                player.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "XDLib" + ChatColor.RED + "]" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "An update is available!");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.RED + "<<< " + ChatColor.WHITE + event.getPlayer().getName());
    }
}