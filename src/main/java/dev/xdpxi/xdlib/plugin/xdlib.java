package dev.xdpxi.xdlib.plugin;

import dev.xdpxi.xdlib.api.plugin.pluginManager;
import org.bukkit.command.*;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public final class xdlib extends JavaPlugin {
    private final HashMap<UUID, UUID> teleportRequests = new HashMap<>();
    private welcomeListener WelcomeListener;
    private chatListener ChatListener;
    private joinLeaveListener JoinLeaveListener;

    @Override
    public void onEnable() {
        getLogger().info("[XDLib] - Enabling...");

        setConfig();

        this.getCommand("xdlib").setTabCompleter(new tabCompleter());

        updateChecker checker = new updateChecker(this);
        checker.checkForUpdate();

        getLogger().info("[XDLib] - Enabled!");
    }

    private void setConfig() {
        saveDefaultConfig();

        boolean enabled = getConfig().getBoolean("enabled");
        if (!enabled) {
            getLogger().info("[XDLib] - Plugin Disabled in Config!");
            pluginManager.disablePlugin("xdlib");
        }

        boolean welcomeMessage = getConfig().getBoolean("welcomeMessage");
        if (WelcomeListener != null) {
            HandlerList.unregisterAll(WelcomeListener);
        }
        if (welcomeMessage) {
            WelcomeListener = new welcomeListener(this);
            getServer().getPluginManager().registerEvents(WelcomeListener, this);
        }

        boolean customChatMessages = getConfig().getBoolean("customChatMessages");
        if (ChatListener != null) {
            HandlerList.unregisterAll(ChatListener);
        }
        if (customChatMessages) {
            ChatListener = new chatListener();
            getServer().getPluginManager().registerEvents(ChatListener, this);
        }

        boolean customJoinMessage = getConfig().getBoolean("customJoinMessages");
        if (JoinLeaveListener != null) {
            HandlerList.unregisterAll(JoinLeaveListener);
        }
        if (customJoinMessage) {
            JoinLeaveListener = new joinLeaveListener(this);
            getServer().getPluginManager().registerEvents(JoinLeaveListener, this);
        }

        boolean tpa = getConfig().getBoolean("tpaCommand");
        if (isCommandAvailable("tpa")) {
            unregisterCommand("tpa");
        }
        if (tpa) {
            tpaCommand tpaCommand = new tpaCommand();
            this.getCommand("tpa").setExecutor(tpaCommand);
            this.getCommand("tpa").setTabCompleter(new tabCompleter());
        }

        getLogger().info("[XDLib] - Config Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("[XDLib] - Disabled!");
    }

    private boolean isCommandAvailable(String name) {
        try {
            return getServer().getPluginCommand(name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void unregisterCommand(String name) {
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());
            commandMap.getCommand(name).unregister(commandMap);
        } catch (Exception e) {
            getLogger().warning("Failed to unregister command: " + name);
        }
    }
}