package dev.xdpxi.xdlib.plugin;

import dev.xdpxi.xdlib.api.plugin.pluginManager;
import org.bukkit.command.*;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public final class xdlib extends JavaPlugin implements CommandExecutor {
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("xdlib")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("xdlib.reload")) {
                        reloadConfig();
                        setConfig();
                        sender.sendMessage("[XDLib] - Config reloaded!");
                    } else {
                        sender.sendMessage("[XDLib] - You do not have permission to execute this command.");
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage("Available commands:\n/xdlib - Displays the configured message\n/xdlib reload - Reloads the plugin configuration\n/xdlib help - Shows this help message");
                } else {
                    sender.sendMessage("[XDLib] - Use '/xdlib help' for a list of commands.");
                }
            } else {
                String message = getConfig().getString("message");
                sender.sendMessage(message);
            }
            return true;
        }
        return false;
    }

    private void unregisterCommand(String commandName) {
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());

            PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.unregister(commandMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCommandAvailable(String commandName) {
        PluginCommand command = getServer().getPluginCommand(commandName);
        return command != null;
    }
}