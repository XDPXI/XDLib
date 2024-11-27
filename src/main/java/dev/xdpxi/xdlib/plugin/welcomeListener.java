package dev.xdpxi.xdlib.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class welcomeListener implements Listener {
    private final xdlib plugin;

    public welcomeListener(xdlib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore() && plugin.getConfig().getBoolean("welcomeMessage")) {
            event.getPlayer().sendMessage(Component.text("Welcome ")
                .color(NamedTextColor.GOLD)
                .append(Component.text(event.getPlayer().getName()).color(NamedTextColor.GREEN))
                .append(Component.text(" to the server!").color(NamedTextColor.GOLD)));
        }
    }
}