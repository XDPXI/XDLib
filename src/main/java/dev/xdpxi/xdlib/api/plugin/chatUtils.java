package dev.xdpxi.xdlib.api.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class chatUtils {
    public static void sendMessageToAll(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void sendMessageToOps(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(message);
            }
        }
    }
}