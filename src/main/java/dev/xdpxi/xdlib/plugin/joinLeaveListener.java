package dev.xdpxi.xdlib.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class joinLeaveListener implements Listener {
    private final xdlib plugin;

    public joinLeaveListener(xdlib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text(">>> " + event.getPlayer().getName())
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.BOLD, false));

        Player player = event.getPlayer();
        if (player.isOp()) {
            if (updateChecker.isUpdate()) {
                player.sendMessage(Component.text()
                    .append(Component.text("[", NamedTextColor.RED))
                    .append(Component.text("XDLib", NamedTextColor.GOLD))
                    .append(Component.text("]", NamedTextColor.RED))
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("An update is available!", NamedTextColor.YELLOW))
                    .build());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(Component.text()
            .append(Component.text("<<< ", NamedTextColor.RED))
            .append(Component.text(event.getPlayer().getName(), NamedTextColor.WHITE))
            .build());
    }
}